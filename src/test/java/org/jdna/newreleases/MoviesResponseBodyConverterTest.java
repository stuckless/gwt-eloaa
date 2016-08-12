package org.jdna.newreleases;

import junit.framework.TestCase;
import org.jdna.eloaa.client.model.GMovie;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by seans on 12/08/16.
 */
public class MoviesResponseBodyConverterTest {

    @Test
    public void testAddMovies() throws Exception {
        List<GMovie> movies = new ArrayList<>();
        MoviesResponseBodyConverter c = new MoviesResponseBodyConverter();
        c.addMovies(movies, this.getClass().getClassLoader().getResourceAsStream("dvdreleases.html"));
        GMovie m = movies.get(0);
        System.out.println("MOVE: " + m);
    }

}