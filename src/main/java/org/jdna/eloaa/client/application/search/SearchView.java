package org.jdna.eloaa.client.application.search;

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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.client.ui.*;
import org.jdna.eloaa.client.application.ApplicationPresenter;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.application.event.SearchEvent;
import org.jdna.eloaa.client.application.event.SearchEventHandler;
import org.jdna.eloaa.client.application.widgets.MovieResult;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.service.EloaaService;

import javax.inject.Inject;
import java.util.List;

public class SearchView extends ViewImpl implements SearchPresenter.MyView, SearchEventHandler {
    private HandlerRegistration searchHandler;

    interface Binder extends UiBinder<Widget, SearchView> {
    }

    @UiField
    MaterialContainer searchContainer;

    @UiField
    MaterialPanel searchContainerInner;

    @UiField
    MaterialProgress searchProgress;

    @Inject
    SearchView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        GApp.get().getEventBus().fireEvent(new SearchEvent(true, null));
        searchHandler = GApp.get().getEventBus().addHandler(SearchEvent.TYPE, this);
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        if (searchHandler!=null) {
            searchHandler.removeHandler();
        }
        GApp.get().getEventBus().fireEvent(new SearchEvent(false, null));
        super.onDetach();
    }

    @Override
    public void onSearch(final SearchEvent event) {
        if (event.isEnableSearch()) {
            if (event.getText()==null) {
                // nothing to do yet
                return;
            }
            GWT.log("Searching " + event.getText());
            searchContainerInner.clear();
            searchProgress.setVisible(true);
            EloaaService.Instance.get().searchMovies(event.getText(), new AsyncCallback<List<GMovie>>() {
                @Override
                public void onFailure(Throwable caught) {
                    MaterialToast.fireToast("Search Failed: " + caught.getMessage());
                }

                @Override
                public void onSuccess(List<GMovie> result) {
                    try {
                        searchProgress.setVisible(false);
                        MaterialToast.fireToast("Showing " + result.size() + " items");
                        for (GMovie m : result) {
                            searchContainerInner.add(new MovieResult(m));
                        }
                        if (result.size() == 0) {
                            MaterialToast.fireToast("Nothing found for '" + event.getText() + "'");
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        GWT.log("ERROR", t);
                    }
                }
            });
        }
    }
}
