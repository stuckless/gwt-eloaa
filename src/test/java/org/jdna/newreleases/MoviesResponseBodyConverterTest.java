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