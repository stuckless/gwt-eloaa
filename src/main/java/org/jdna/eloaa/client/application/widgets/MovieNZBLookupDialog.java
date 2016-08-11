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
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTitle;
import gwt.material.design.client.ui.MaterialToast;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.service.EloaaService;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

import java.util.List;

/**
 * Created by seans on 08/08/16.
 */
public class MovieNZBLookupDialog extends Composite {
    GMovie movie;

    interface MovieNZBLookupDialogUiBinder extends UiBinder<MaterialModal, MovieNZBLookupDialog> {
    }

    private static MovieNZBLookupDialogUiBinder ourUiBinder = GWT.create(MovieNZBLookupDialogUiBinder.class);

    @UiField
    MaterialModal modal;

    @UiField
    MaterialTitle title;

    @UiField
    MaterialPanel items;

    public MovieNZBLookupDialog() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public MovieNZBLookupDialog(GMovie movie) {
        super();
        setMovie(movie);
    }

    public void setMovie(final GMovie movie) {
        this.movie=movie;
        title.setTitle("Lookup for " + movie.getFullTitle());
        items.clear();

        modal.openModal();

        EloaaService.Instance.get().performMovieNZBLookup(movie, new AsyncCallback<GResponse<List<NzbItem>>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Unable to search");
            }

            @Override
            public void onSuccess(GResponse<List<NzbItem>> result) {
                if (result.isOK()) {
                    for (NzbItem i: result.get()) {
                        items.add(new NZBRowItem(i, movie));
                    }
                } else {
                    MaterialToast.fireToast("No Results");
                }
            }
        });
    }

    public void close() {
        modal.closeModal();
    }


    @UiHandler("close")
    public void onClose(ClickEvent event) {
        modal.closeModal();
    }
}