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


import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.client.events.SearchFinishEvent;
import gwt.material.design.client.ui.MaterialContainer;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialSearch;
import org.jdna.eloaa.client.application.event.MovieAdded;
import org.jdna.eloaa.client.application.event.MovieAddedHandler;
import org.jdna.eloaa.client.application.event.SearchEvent;
import org.jdna.eloaa.client.application.event.SearchEventHandler;

import javax.inject.Inject;

public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

    interface Binder extends UiBinder<Widget, ApplicationView> {
    }

    @UiField
    HTMLPanel main;

    @UiField
    MaterialNavBar navBar, navBarSearch;

    @UiField
    MaterialSearch txtSearch;

    @Inject
    ApplicationView(
            Binder uiBinder, final EventBus bus) {
        initWidget(uiBinder.createAndBindUi(this));

        bindSlot(ApplicationPresenter.SLOT_MAIN, main);

        txtSearch.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    GApp.get().getEventBus().fireEvent(new SearchEvent(true, txtSearch.getText()));
                }
            }
        });

        GApp.get().getEventBus().addHandler(SearchEvent.TYPE, new SearchEventHandler() {
            @Override
            public void onSearch(SearchEvent event) {
                if (event.isEnableSearch()) {
                    enableSearch();
                } else {
                    disableSearch();
                }
            }
        });

        GApp.get().getEventBus().addHandler(MovieAdded.TYPE, new MovieAddedHandler() {
            @Override
            public void onMovieAdded(MovieAdded event) {
                // just show the /movies url
                History.newItem("/movies");
                History.fireCurrentHistoryState();
            }
        });

        GApp.get().reset();
    }

    @Override
    public void enableSearch() {
        navBar.setVisible(false);
        navBarSearch.setVisible(true);
    }

    @Override
    public void disableSearch() {
        navBar.setVisible(true);
        navBarSearch.setVisible(false);
    }
}
