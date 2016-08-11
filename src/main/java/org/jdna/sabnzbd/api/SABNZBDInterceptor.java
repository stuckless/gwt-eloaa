package org.jdna.sabnzbd.api;

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

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * {@link Interceptor} to add the API key query parameter. As it modifies the URL ensure this is added as regular
 * interceptor, otherwise caching will be broken.
 */
public class SABNZBDInterceptor implements Interceptor {

    private SABNZBD sab;

    public SABNZBDInterceptor(SABNZBD sab) {
        this.sab=sab;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return handleIntercept(chain, sab);
    }

    /**
     * If the host matches nzbs host adds a query parameter with the API key.
     */
    public static Response handleIntercept(Chain chain, SABNZBD sab) throws IOException {
        Request request = chain.request();
        if (!request.url().toString().startsWith(sab.url())) {
            // do not intercept requests for other hosts
            // this allows the interceptor to be used on a shared okhttp client
            return chain.proceed(request);
        }

        // add (or replace) the API key query parameter
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        urlBuilder.setEncodedQueryParameter(SABNZBD.PARAM_API_KEY, sab.apiKey());
        urlBuilder.setEncodedQueryParameter("output", "json");

        Request.Builder builder = request.newBuilder();
        builder.url(urlBuilder.build());

        return chain.proceed(builder.build());
    }
}
