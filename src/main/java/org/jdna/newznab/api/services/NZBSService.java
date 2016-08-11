package org.jdna.newznab.api.services;

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
import org.jdna.newznab.api.model.Capabilities;
import org.jdna.newznab.api.model.SearchResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by seans on 03/08/16.
 */
public interface NZBSService {
    @GET("api?t=caps")
    Call<Capabilities> capabilities();

    @GET("api?t=search&sort=size_asc&extended=1")
    Call<SearchResults> search(@Query("q") String query);

    /**
     * @param imdbid imdbid withoug the 'tt' prefix
     */
    @GET("api?t=movie&sort=size_asc&extended=1")
    Call<SearchResults> movies(@Query("imdbid") String imdbid);

    @GET("api?t=book&extended=1")
    Call<SearchResults> books(@Query("author") String author);

    @GET("api?t=tv&extended=1")
    Call<SearchResults> tv(@Query("q") String query, @Query("season") String season, @Query("ep") String episode);

    @GET("api?t=tv&extended=1")
    Call<SearchResults> tv(@Query("tvdbid") long tvdbid, @Query("season") String season, @Query("ep") String episode);

    @GET("api?t=comments")
    Call<SearchResults> comments(@Query("id") String guid);

    @GET("api?t=details")
    Call<SearchResults> details(@Query("id") String guid);

    @GET("api?t=get")
    Call<ResponseBody> download(@Query("id") String guid);
}
