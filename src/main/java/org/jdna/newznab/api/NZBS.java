package org.jdna.newznab.api;

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
import org.jdna.newznab.api.services.NZBSService;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by seans on 03/08/16.
 */
public class NZBS {
    /**
     * API key query parameter name.
     */
    public static final String PARAM_API_KEY = "apikey";
    private String apiKey;
    private String url;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;


    /**
     * Create a new manager instance using the base url and key
     *
     * @param apiKey your nzbs.pl api key
     */
    public NZBS(String url, String apiKey) {
        this.apiKey = apiKey;
        this.url = url;
    }

    public void apiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String apiKey() {
        return apiKey;
    }

    public String url() {
        return url;
    }

    /**
     * Creates a {@link Retrofit.Builder} that sets the base URL, adds a Gson converter and sets {@link #okHttpClient()}
     * as its client.
     *
     * @see #okHttpClient()
     */
    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(SimpleXmlConverterFactory.create())
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
        builder.addInterceptor(new NZBSInterceptor(this));
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


    public NZBSService NZBSService() {
        return getRetrofit().create(NZBSService.class);
    }
}
