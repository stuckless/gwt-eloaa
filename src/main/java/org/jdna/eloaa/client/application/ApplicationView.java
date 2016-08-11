package org.jdna.eloaa.client.application;

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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.client.events.SearchFinishEvent;
import gwt.material.design.client.ui.*;
import org.jdna.eloaa.client.application.event.*;
import org.jdna.eloaa.client.application.widgets.DBMovieEntry;
import org.jdna.eloaa.client.application.widgets.MovieNZBLookupDialog;
import org.jdna.eloaa.client.application.widgets.MovieResult;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.service.EloaaService;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

import javax.inject.Inject;
import java.util.List;

public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

    interface Binder extends UiBinder<Widget, ApplicationView> {
    }

    @UiField
    MaterialNavBar navBar, navBarSearch;

    @UiField
    MaterialSearch txtSearch;

    @UiField
    MaterialContainer mainContainer;

    @UiField
    MaterialContainer searchContainer;

    @UiField
    MaterialContainer searchContainerInner;

    @UiField
    MaterialPreLoader searchProgress;

    @UiField
    MaterialCollapsible dbMovies;

    @UiField
    MovieNZBLookupDialog nzbLookupDialog;

    @UiField
    HTMLPanel main;

    @Inject
    ApplicationView(
            Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        txtSearch.addCloseHandler(new CloseHandler<String>() {
            @Override
            public void onClose(CloseEvent<String> event) {
                navBar.setVisible(true);
                navBarSearch.setVisible(false);
                toggleSearchResults(false, false);
            }
        });
        txtSearch.addSearchFinishHandler(new SearchFinishEvent.SearchFinishHandler() {
            @Override
            public void onSearchFinish(SearchFinishEvent event) {
                toggleSearchResults(false, false);
            }
        });
        txtSearch.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    MaterialToast.fireToast("Searching...");
                    setSearches(txtSearch.getText());
                }
            }
        });

        GApp.get().reset();
        GApp.get().registerEventHander(MovieAdded.TYPE, new MovieAddedHandler() {
            @Override
            public void onMovieAdded(MovieAdded event) {
                toggleSearchResults(false, false);
                dbMovies.insert(new DBMovieEntry(event.getMovie()),0);
            }
        });

        GApp.get().registerEventHander(MovieNZBSearch.TYPE, new MovieNZBSearchHandler() {
            @Override
            public void onMovieNZBSearch(MovieNZBSearch event) {
                doMovieDownloadLookup(event.getMovie());
            }
        });

        GApp.get().registerEventHander(DownloadMovie.TYPE, new DownloadMovieHandler() {
            @Override
            public void onDownloadMovie(DownloadMovie event) {
                processMovieDownload(event.getItem(), event.getMovie());
            }
        });

        refreshDBMovies();
    }

    private void processMovieDownload(NzbItem item, GMovie movie) {
        nzbLookupDialog.close();
        MaterialToast.fireToast("Downloading " + movie.getTitle());
        EloaaService.Instance.get().downloadMovie(item, movie, new AsyncCallback<GResponse<GMovie>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failed to download");
            }

            @Override
            public void onSuccess(GResponse<GMovie> result) {
                GMovie movie = result.get();
                if (movie==null) {
                    MaterialToast.fireToast("Unable to download");
                    return;
                }

                updateMovieItem(movie);
            }
        });
    }

    private void updateMovieItem(GMovie movie) {
        GMovie widgetMovie;
        for (Widget w: dbMovies) {
            if (w instanceof DBMovieEntry) {
                widgetMovie = ((DBMovieEntry)w).getMovie();
            }
        }
    }

    private void doMovieDownloadLookup(GMovie movie) {
        nzbLookupDialog.setMovie(movie);
    }

    void toggleSearchResults(boolean searchVisible, boolean progress) {
        mainContainer.setVisible(!searchVisible);
        searchContainer.setVisible(searchVisible);
        searchProgress.setVisible(progress);
        if (!searchVisible) {
            navBar.setVisible(true);
            navBarSearch.setVisible(false);
        }
    }

    private void setSearches(String text) {
        GWT.log("Searching: " + text);
        searchContainerInner.clear();
        toggleSearchResults(true, true);
        EloaaService.Instance.get().searchMovies(text, new AsyncCallback<List<GMovie>>() {
            @Override
            public void onFailure(Throwable caught) {
                toggleSearchResults(false, false);
                MaterialToast.fireToast("Search Failed: " + caught.getMessage());
            }

            @Override
            public void onSuccess(List<GMovie> result) {
                searchProgress.setVisible(false);
                for (GMovie m: result) {
                    searchContainerInner.add(new MovieResult(m));
                }
                if (result.size()==0) {
                    toggleSearchResults(false,false);
                    MaterialToast.fireToast("Nothing found");
                }
            }
        });
    }

    @UiHandler("btnSearch")
    void onSearch(ClickEvent e) {
        navBar.setVisible(false);
        navBarSearch.setVisible(true);
    }
    @UiHandler("btnSearch2")
    void onSearch2(ClickEvent e) {
        navBar.setVisible(false);
        navBarSearch.setVisible(true);
    }

    @Override
    public void onGenerateListSeach() {
    }

    void refreshDBMovies() {
        EloaaService.Instance.get().getMovies(new AsyncCallback<GResponse<List<GMovie>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GResponse<List<GMovie>> result) {
                dbMovies.clear();
                if (!result.isOK()) {
                    MaterialToast.fireToast("Failed to load movies");
                } else {
                    for (GMovie m: result.get()) {
                        dbMovies.add(new DBMovieEntry(m));
                    }
                }
            }
        });
    }
}
