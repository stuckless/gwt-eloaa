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

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by seans on 03/08/16.
 */
public class NewReleases {
    public static final String BASE_URL = "http://www.dvdsreleasedates.com/";
    private String url;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;


    /**
     * Create a new manager instance using the base url and key
     */
    public NewReleases() {
        this.url = BASE_URL;
    }

    public String url() {
        return url;
    }

    /**
     * @see #okHttpClient()
     */
    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(DvdReleaseDatesFactory.create())
                .client(okHttpClient());
    }


    /**
     * Returns the default OkHttp client instance. It is strongly recommended to override this and use your app
     * instance.
     *
     * @see #setOkHttpClientDefaults(OkHttpClient.Builder)
     */
    protected synchronized OkHttpClient okHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            setOkHttpClientDefaults(builder);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * Adds an interceptor to add the api key query parameter and to log requests.
     */
    protected void setOkHttpClientDefaults(OkHttpClient.Builder builder) {
        //builder.addInterceptor(new NZBSInterceptor(this));
    }

    /**
     * Return the current {@link Retrofit} instance. If none exists (first call, auth changed), builds a new one.
     * <p>When building, sets the base url and a custom client with an {@link okhttp3.Interceptor} which supplies authentication
     * data.
     */
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }


    public DVDReleaseDatesService DVDReleaseDatesService() {
        return getRetrofit().create(DVDReleaseDatesService.class);
    }
}
