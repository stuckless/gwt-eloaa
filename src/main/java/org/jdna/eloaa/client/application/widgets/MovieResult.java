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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import org.jdna.eloaa.shared.model.GMovie;
import org.jdna.eloaa.client.util.UIUtils;

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
        UIUtils.setOpenIMDBHandler(movie, poster);
    }

    @UiHandler("download")
    public void download(ClickEvent evt) {
        UIUtils.addMovie(movie);
    }

    @UiHandler("imdb")
    public void imdb(ClickEvent evt) {
        UIUtils.openIMDBHandler(movie).onClick(evt);
    }

}