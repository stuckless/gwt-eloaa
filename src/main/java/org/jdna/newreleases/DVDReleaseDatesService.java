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

import org.jdna.eloaa.client.model.GMovie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by seans on 08/11/14.
 */
public interface DVDReleaseDatesService {
    /**
     *
     * @param year
     * @param intMonth 1=January
     * @param strMonth
     * @return
     */
    @GET("/releases/{year}/{intmonth}/new-dvd-releases-{intmonth}-{year}")
    Call<List<GMovie>> getMovies(@Path("year") int year, @Path("intmonth") int intMonth);
}
