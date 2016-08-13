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
import com.uwetrottmann.tmdb2.entities.FindResults;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.ReleaseDatesResults;
import com.uwetrottmann.tmdb2.enumerations.ExternalSource;
import com.uwetrottmann.tmdb2.services.SearchService;
import gwt.material.design.client.ui.MaterialToast;
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
import org.jdna.newznab.api.model.Capabilities;
import org.jdna.newznab.api.model.SearchResultItem;
import org.jdna.newznab.api.model.SearchResults;
import org.jdna.sabnzbd.api.model.AddStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
                GMovie mn = tmdbMovieToGMovie(m, false);
                movies.add(mn);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    @Override
    public GResponse<GMovie> addMovie(GMovie movie) {
        System.out.println("Adding Movie: " + movie);
        try {
            if (movie.getId()==null) {
                System.out.println("Finding Movie based on IMDBID: " + movie.getImdbid());
                movie = findMovie(movie, true);
            }
            App.get().getDbManager().addNewMovie(movie);
        } catch (SQLException e) {
            return new GResponse<>(Responses.ERR_SQL, e.getMessage());
        } catch (DBException e) {
            return new GResponse<>(Responses.ERR_ALREADY_EXISTS, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return new GResponse<>(1, e.getMessage());
        }

        return new GResponse<>(movie);
    }

    GMovie tmdbMovieToGMovie(Movie m, boolean extraDetails) {
        GMovie mn = new GMovie();
        mn.setTitle(m.title);
        mn.setDescription(m.overview);
        mn.setId(m.id.toString());
        if (mn.getImdbid()==null) {
            mn.setImdbid(m.imdb_id);
        }
        if (m.release_date!=null) {
            Calendar c = Calendar.getInstance();
            c.setTime(m.release_date);
            mn.setYear(""+c.get(Calendar.YEAR));
        }
        mn.setPosterUrl(org.jdna.eloaa.server.App.get().getPosterBaseUrl() + m.poster_path);
        mn.setDateAdded(new Date());
        if (extraDetails) {
            try {
                upgradeMovie(mn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mn.getReleaseDate()==null) {
            mn.setReleaseDate(m.release_date);
        }
        return mn;
    }

    private void upgradeMovie(GMovie movie) throws IOException {
        if (movie.getImdbid()==null || movie.getReleaseDate()==null) {
            System.out.println("Upgrading Movie Details: " + movie);
            Call<Movie> call = App.get().getIMDBLookupService().summary(Integer.parseInt(movie.getId()), "en", null);
            Movie m = call.execute().body();
            movie.setImdbid(m.imdb_id);
            if (m.release_date != null) {
                movie.setReleaseDate(m.release_date);
            }
        }
    }

    private GMovie findMovie(GMovie movie, boolean upgrade) throws IOException {
        if (movie.getId()!=null) return movie;

        FindResults results = App.get().getTMDB().findService().find(movie.getImdbid(), ExternalSource.IMDB_ID, "en").execute().body();
        List<Movie> movies = results.movie_results;
        if (upgrade) {
            upgrade = movie.getImdbid() == null || movie.getReleaseDate() == null;
        }
        if (movies.size()>0) {
            GMovie newMovie = tmdbMovieToGMovie(movies.get(0), upgrade);
            if (movie.getImdbid()!=null) {
                newMovie.setImdbid(movie.getImdbid());
            }
            if (movie.getReleaseDate()!=null) {
                newMovie.setReleaseDate(movie.getReleaseDate());
            }
            return newMovie;
        }
        return null;
    }

    @Override
    public GResponse<List<GMovie>> getMovies() {
        QueryBuilder<MovieEntry, String> queryBuilder = App.get().getDbManager().getMoviesDao().queryBuilder();
        queryBuilder.orderBy("downloaded", true);
        queryBuilder.orderBy("releaseDate", true);
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
        if (movie.getImdbid()==null || movie.getReleaseDate()==null) {
            try {
                upgradeMovie(movie);
            } catch (IOException e) {
                return new GResponse<>("Unable to find IMDB for movie");
            }

            if (movie.getImdbid()==null) {
                return new GResponse<>("Failed to get imdb id for movie");
            }

            MovieEntry me = null;
            try {
                me = App.get().getDbManager().getMoviesDao().queryForId(movie.getId());
                if (movie.getImdbid()!=null) {
                    me.setImdbID(movie.getImdbid());
                }
                if (movie.getReleaseDate()!=null) {
                    me.setReleaseDate(movie.getReleaseDate());
                }
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

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        try {
            SearchResults results = App.get().getNZBIndexService().movies(imdbid).execute().body();
            List<NzbItem> items = new ArrayList<>();
            if (results.getItems()!=null) {
                for (SearchResultItem sr: results.getItems()) {
                    NzbItem i = new NzbItem();
                    i.setTitle(sr.getTitle());
                    i.setGUID(sr.getGUID());
                    i.setSize(sr.getSize());
                    i.setUsenetDate(parseDate(sdf, sr.getUsenetDate()));
                    if (i.getUsenetDate()!=null) {
                        i.setAge(getDifferenceDays(i.getUsenetDate(), new Date()));
                    }
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

    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private Date parseDate(SimpleDateFormat sdf, String usenetDate) {
        try {
            return sdf.parse(usenetDate);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public GResponse<GMovie> downloadMovie(final NzbItem item, GMovie movie) {
        try {
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

    @Override
    public GResponse<List<GMovie>> newReleases(Integer year, Integer month) {
        if (year==null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (month==null) {
            month = Calendar.getInstance().get(Calendar.MONTH)+1;
        }
        try {
            List<GMovie> movies = App.get().getNewReleasesService().getMovies(year, month).execute().body();
            return new GResponse<>(movies);
        } catch (IOException e) {
            return new GResponse<>(1,"Failed to get new releases");
        }
    }

    @Override
    public GResponse<String> findIMDBID(String id) {
        Call<Movie> call = App.get().getIMDBLookupService().summary(Integer.parseInt(id), "en", null);
        try {
            return new GResponse<>(0,"",call.execute().body().imdb_id);
        } catch (IOException e) {
            e.printStackTrace();
            return new GResponse<>(1,e.getMessage(),null);
        }
    }
}