package org.jdna.newreleases;

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

import okhttp3.ResponseBody;
import org.jdna.eloaa.client.model.GMovie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by seans on 12/08/16.
 */
public class MoviesResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Pattern IMDBPattern = Pattern.compile("title/(tt[0-9a-z]+)");
    private static final Pattern datePattern = Pattern.compile("([a-z]+)\\s+([0-9]{1,2})\\s*,\\s*([0-9]{4})", Pattern.CASE_INSENSITIVE);
    private static final SimpleDateFormat dateParser = new SimpleDateFormat("MMMMM dd, yyyy");

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        List<GMovie> movies = new ArrayList<>();

        addMovies(movies, responseBody.byteStream());

        return (T)movies;
    }

    void addMovies(List<GMovie> movies, InputStream reader) throws IOException {
        Document doc = Jsoup.parse(reader, "UTF-8", NewReleases.BASE_URL);
        Elements els = doc.select(".reldate");
        for (Element e : els) {
            Calendar dvdReleaseDate = parseDate(e.text());

            Element table = getTable(e);
            Elements cells = table.select(".dvdcell");
            for (Element cell : cells) {
                GMovie movie = new GMovie();

                Element poster = cell.select(".movieimg").first();
                if (poster!=null) {
                    movie.setPosterUrl(poster.attr("src"));
                }

                Elements links = cell.select("a");
                for (Element link : links) {
                    if (movie.getImdbid()==null && link.attr("href") != null && link.attr("href").contains(".imdb.")) {
                        movie.setImdbid(parseIMDB(link.attr("href")));
                    } else if (movie.getTitle()==null && link.text() != null && link.text().trim().length() > 0) {
                        movie.setTitle(link.text().trim());
                    }
                    if (movie.getImdbid() != null && movie.getTitle() != null) break;
                }

                if (dvdReleaseDate!=null) {
                    movie.setYear(""+dvdReleaseDate.get(Calendar.YEAR));
                    movie.setReleaseDate(dvdReleaseDate.getTime());
                }

                if (movie.getImdbid()!=null) {
                    movies.add(movie);
                }
            }
        }
    }

    String parseIMDB(String href) {
        Matcher m = IMDBPattern.matcher(href);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Element getTable(Element e) {
        if (e == null) return null;
        if (e.nodeName().equalsIgnoreCase("table")) return e;
        return getTable(e.parent());
    }

    Calendar parseDate(String text) {
        Matcher strDateMatcher = datePattern.matcher(text);
        if (strDateMatcher.find()) {
            String month = strDateMatcher.group(1);
            String day = strDateMatcher.group(2);
            String year = strDateMatcher.group(3);
            try {
                Date d = dateParser.parse(month + " " + day + ", " + year);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(d.getTime());
                return cal;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
