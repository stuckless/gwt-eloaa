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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.jdna.eloaa.shared.model.GMovie;
import org.jdna.eloaa.shared.model.GProgress;
import org.jdna.eloaa.shared.model.GResponse;
import org.jdna.eloaa.shared.model.HistoryItem;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

import java.util.List;
import java.util.Map;

/**
 * Created by seans on 04/08/16.
 */
@RemoteServiceRelativePath("EloaaService")
public interface EloaaService extends RemoteService {
    /**
     * Utility/Convenience class.
     * Use EloaaService.This.get() to access static instance of EloaaServiceAsync
     */
    public static class Instance {
        private static final EloaaServiceAsync ourInstance = (EloaaServiceAsync) GWT.create(EloaaService.class);

        public static EloaaServiceAsync get() {
            return ourInstance;
        }
    }

    public List<GMovie> searchMovies(String query);

    public GResponse<GMovie> addMovie(GMovie movie);

    public GResponse<List<GMovie>> getMovies();

    public GResponse<List<NzbItem>> performMovieNZBLookup(GMovie movie);

    public GResponse<GMovie> downloadMovie(NzbItem item, GMovie movie);

    public GResponse<GProgress> getDownloadMovieProgress(String mid);

    public GResponse<Boolean> deleteMovie(GMovie movie);

    public GResponse<Boolean> cancelDownload(GMovie movie);

    public GResponse<Boolean> removeMovie(GMovie movie);

    public GResponse<Map<String,String>> getProperties();
    public GResponse<Boolean> updateProperties(Map<String,String> props);

    public GResponse<List<GMovie>> newReleases(Integer year, Integer month);

    public GResponse<String> findIMDBID(String id);

    public GResponse<List<HistoryItem>> getHistory(GMovie movie);
}
