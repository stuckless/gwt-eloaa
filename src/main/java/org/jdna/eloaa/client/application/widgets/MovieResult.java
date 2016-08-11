package org.jdna.eloaa.client.application.widgets;

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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialToast;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.application.event.MovieAdded;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.model.Responses;
import org.jdna.eloaa.client.service.EloaaService;

/**
 * Created by seans on 05/08/16.
 */
public class MovieResult extends Composite {
    private final GMovie movie;

    interface MovieResultUiBinder extends UiBinder<Widget, MovieResult> {
    }

    private static MovieResultUiBinder ourUiBinder = GWT.create(MovieResultUiBinder.class);

    @UiField
    MaterialLabel desc;

    @UiField
    MaterialCardTitle title;

    @UiField
    MaterialImage poster;

    public MovieResult(GMovie movie) {
        this.movie=movie;
        initWidget(ourUiBinder.createAndBindUi(this));
        title.setText(movie.getFullTitle());
        desc.setText(movie.getDescription());
        poster.setUrl(movie.getPosterUrl());
    }

    @UiHandler("download")
    public void download(ClickEvent evt) {
        EloaaService.Instance.get().addMovie(movie, new AsyncCallback<GResponse<GMovie>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Error: "+ caught);
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(GResponse<GMovie> result) {
                if (result.getCode()!= Responses.OK) {
                    MaterialToast.fireToast("Failed to add movie: " + result.getMsg());
                } else {
                    MaterialToast.fireToast("Added " + movie.getTitle());
                    GApp.get().getEventBus().fireEvent(new MovieAdded(movie));
                }
            }
        });
    }
}