package org.jdna.eloaa.client.service;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GProgress;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

import java.util.List;
import java.util.Map;

/**
 * Created by seans on 04/08/16.
 */
public interface EloaaServiceAsync {
    void searchMovies(String query, AsyncCallback<List<GMovie>> async);

    void addMovie(GMovie movie, AsyncCallback<GResponse<GMovie>> async);

    void getMovies(AsyncCallback<GResponse<List<GMovie>>> async);

    void performMovieNZBLookup(GMovie movie, AsyncCallback<GResponse<List<NzbItem>>> async);

    void downloadMovie(NzbItem item, GMovie movie, AsyncCallback<GResponse<GMovie>> async);

    void getDownloadMovieProgress(String mid, AsyncCallback<GResponse<GProgress>> async);

    void deleteMovie(GMovie movie, AsyncCallback<GResponse<Boolean>> async);

    void removeMovie(GMovie movie, AsyncCallback<GResponse<Boolean>> async);

    void getProperties(AsyncCallback<GResponse<Map<String, String>>> async);

    void updateProperties(Map<String, String> props, AsyncCallback<GResponse<Boolean>> async);

    void newReleases(Integer year, Integer month, AsyncCallback<GResponse<List<GMovie>>> async);

    void findIMDBID(String id, AsyncCallback<GResponse<String>> async);
}
