package org.jdna.sabnzbd.api.services;

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

import okhttp3.RequestBody;
import org.jdna.sabnzbd.api.model.AddStatus;
import org.jdna.sabnzbd.api.model.QResponse;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by seans on 08/08/16.
 */
public interface SABNZBDService {
    @Multipart
    @POST("api?mode=addfile")
    Call<AddStatus> addFile(@Part("name") RequestBody file);

    @GET("api?mode=addurl")
    Call<AddStatus> addUrl(@Query("name") String url);

    @GET("api?mode=queue")
    Call<QResponse> queue();

    @GET("api?mode=history")
    Call<QResponse> history();

    @GET("api?mode=queue&name=delete")
    Call<Void> remove(@Query("value") String nzo_id);

    @GET("api?mode=queue&name=delete&del_files=1")
    Call<Void> delete(@Query("value") String nzo_id);
}
