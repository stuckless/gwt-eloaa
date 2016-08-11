package org.jdna.eloaa.server.service;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.j256.ormlite.stmt.QueryBuilder;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.SearchService;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GProgress;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.model.Responses;
import org.jdna.eloaa.client.service.EloaaService;
import org.jdna.eloaa.server.App;
import org.jdna.eloaa.server.db.DBException;
import org.jdna.eloaa.server.db.MovieEntry;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;
import org.jdna.newznab.api.NZBSHelper;
import org.jdna.newznab.api.model.SearchResultItem;
import org.jdna.newznab.api.model.SearchResults;
import org.jdna.sabnzbd.api.model.AddStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by seans on 04/08/16.
 */
public class EloaaServiceImpl extends RemoteServiceServlet implements EloaaService {
    @Override
    public List<GMovie> searchMovies(String query) {
        List<GMovie> movies = new ArrayList<>();
        try {
            SearchService searchService = org.jdna.eloaa.server.App.get().getIMDBSearchService();
            Call<MovieResultsPage> call = searchService.movie(query, null, "en", null, null, null, null);
            Response<MovieResultsPage> response = call.execute();
            MovieResultsPage page = response.body();
            for (Movie m : page.results) {
                GMovie mn = new GMovie();
                mn.setTitle(m.title);
                mn.setDescription(m.overview);
                mn.setId(m.id.toString());
                mn.setImdbid(m.imdb_id);
                if (m.release_date!=null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(m.release_date);
                    mn.setYear(""+c.get(Calendar.YEAR));
                }
                mn.setPosterUrl(org.jdna.eloaa.server.App.get().getPosterBaseUrl() + m.poster_path);
                movies.add(mn);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    @Override
    public GResponse<GMovie> addMovie(GMovie movie) {
        try {
            App.get().getDbManager().addNewMovie(movie);
        } catch (SQLException e) {
            return new GResponse<>(Responses.ERR_SQL, e.getMessage());
        } catch (DBException e) {
            return new GResponse<>(Responses.ERR_ALREADY_EXISTS, e.getMessage());
        }

        return new GResponse<>(movie);
    }

    @Override
    public GResponse<List<GMovie>> getMovies() {
        QueryBuilder<MovieEntry, String> queryBuilder = App.get().getDbManager().getMoviesDao().queryBuilder();
        queryBuilder.orderBy("dateAdded", false);
        try {
            List<GMovie> retMovies = new ArrayList<>();
            List<MovieEntry> movies = queryBuilder.query();
            for (MovieEntry me: movies) {
                retMovies.add(me.toGMovie());
            }
            return new GResponse<>(retMovies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new GResponse<>("Failed to get movies");
    }

    @Override
    public GResponse<List<NzbItem>> performMovieNZBLookup(GMovie movie) {
        if (movie.getImdbid()==null) {
            // find the imdb id
            Call<Movie> call = App.get().getIMDBLookupService().summary(Integer.parseInt(movie.getId()), "en", null);
            try {
                movie.setImdbid(call.execute().body().imdb_id);
            } catch (IOException e) {
                e.printStackTrace();
                return new GResponse<>("Unable to find imdb id");
            }

            if (movie.getImdbid()==null) {
                return new GResponse<>("Failed to get imdb id for movie");
            }

            MovieEntry me = null;
            try {
                me = App.get().getDbManager().getMoviesDao().queryForId(movie.getId());
                me.setImdbID(movie.getImdbid());
                App.get().getDbManager().getMoviesDao().update(me);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String imdbid = movie.getImdbid();
        if (imdbid.startsWith("tt")) {
            imdbid = imdbid.substring(2);
        }

        System.out.println("SEARCHING: " + imdbid + "; " + movie.getImdbid() ) ;

        try {
            SearchResults results = App.get().getNZBIndexService().movies(imdbid).execute().body();
            List<NzbItem> items = new ArrayList<>();
            if (results.getItems()!=null) {
                for (SearchResultItem sr: results.getItems()) {
                    NzbItem i = new NzbItem();
                    i.setTitle(sr.getTitle());
                    i.setGUID(sr.getGUID());
                    i.setSize(sr.getSize());
                    i.setUsenetDate(sr.getUsenetDate());
                    i.setDescription(sr.getDescription());
                    items.add(i);
                }
            }

            Collections.sort(items, new Comparator<NzbItem>() {
                @Override
                public int compare(NzbItem nzbItem, NzbItem t1) {
                    return Long.compare(nzbItem.getSize(), t1.getSize());
                }
            });

            return new GResponse<>(items);
        } catch (IOException e) {
            e.printStackTrace();
            return new GResponse<>("NZB index search failed");
        }
    }

    @Override
    public GResponse<GMovie> downloadMovie(final NzbItem item, GMovie movie) {
        try {
//            final ResponseBody body = App.get().getNZBIndexService().download(item.getGUID()).execute().body();
//
//            RequestBody upload = new RequestBody() {
//                @Override
//                public MediaType contentType() {
//                    return MediaType.parse("application/x-nzb");
//                }
//
//                @Override
//                public void writeTo(BufferedSink bufferedSink) throws IOException {
//                    bufferedSink.write(Utils.getBytesFromInputStream(body.byteStream()));
//                    bufferedSink.flush();
//                }
//            };
//
//            AddStatus status = App.get().getSABNZBDService().addFile(upload).execute().body();

            AddStatus status = App.get().getSABNZBDService().addUrl(
                    NZBSHelper.getNZBUrl(App.get().getNZBS(), item)).execute().body();
            System.out.println("STATUS: " + status);

            if (!status.status) {
                return new GResponse<>("Unable to queue download to the download service");
            }

            movie.setDownloadToken(status.getDownloadToken());

            MovieEntry me = null;
            try {
                me = App.get().getDbManager().getMoviesDao().queryForId(movie.getId());
                me.setDownloadToken(movie.getDownloadToken());
                App.get().getDbManager().getMoviesDao().update(me);

                // monitor it...
                App.get().getQueueMonitor().monitor(me);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new GResponse<>(movie);
        } catch (Throwable e) {
            e.printStackTrace();
            return new GResponse<>("Failed to create download");
        }
    }

    @Override
    public GResponse<GProgress> getDownloadMovieProgress(String mid) {
        GProgress progress = App.get().getQueueMonitor().getProgress(mid);
        return new GResponse<>(progress);
    }

    @Override
    public GResponse<Boolean> deleteMovie(GMovie movie) {
        try {
            if (movie.getDownloadToken() != null) {
                App.get().getSABNZBDService().delete(movie.getDownloadToken()).execute();
            }
            App.get().getQueueMonitor().unmonitor(movie.getId());
            App.get().getDbManager().delete(movie.getId());
            return new GResponse<>(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new GResponse<>(false);
    }

    @Override
    public GResponse<Boolean> removeMovie(GMovie movie) {
        try {
            if (movie.getDownloadToken()!=null) {
                    App.get().getSABNZBDService().remove(movie.getDownloadToken()).execute();
            }
            App.get().getQueueMonitor().unmonitor(movie.getId());
            App.get().getDbManager().delete(movie.getId());
            return new GResponse<>(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GResponse<>(false);
    }

    @Override
    public GResponse<Map<String, String>> getProperties() {
        Map<String,String> props = new HashMap<>();
        for (String s: App.getPropertyKeys()) {
            props.put(s, App.get().getProperty(s,""));
        }
        return new GResponse<>(props);
    }

    @Override
    public GResponse<Boolean> updateProperties(Map<String, String> props) {
        try {
            for (Map.Entry<String, String> me : props.entrySet()) {
                System.out.println("Updating: " + me.getKey() + "=" + me.getValue());
                App.get().getProperties().setProperty(me.getKey(), me.getValue());
            }
            App.get().propertiesChanged(props);
            return new GResponse<>(true);
        } catch (Throwable t) {
            t.printStackTrace();
            return new GResponse<>(100, "Failed to save", false);
        }
    }
}