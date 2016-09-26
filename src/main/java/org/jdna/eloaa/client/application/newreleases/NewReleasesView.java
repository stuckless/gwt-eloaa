package org.jdna.eloaa.client.application.newreleases;

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
import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.client.ui.*;
import org.jdna.eloaa.shared.model.GMovie;
import org.jdna.eloaa.shared.model.GResponse;
import org.jdna.eloaa.client.service.EloaaService;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class NewReleasesView extends ViewImpl implements NewReleasesPresenter.MyView {
    private HandlerRegistration searchHandler;

    interface Binder extends UiBinder<Widget, NewReleasesView> {
    }

    @UiField
    MaterialContainer searchContainer;

    @UiField
    MaterialPanel searchContainerInner;

    @UiField
    MaterialProgress searchProgress;

    @Inject
    NewReleasesView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    int year = 0, month = 0;

    @Override
    protected void onAttach() {
        super.onAttach();
        if (searchContainerInner.getWidgetCount()==0) {
            doSearch();
        }
    }

    @UiHandler("next")
    public void next(ClickEvent evt) {
        addMonth(1);
    }

    private void addMonth(int i) {
        month = month + i;
        if (month < 1) {
            month = 12;
            year = year - 1;
        }
        if (month > 12) {
            month = 1;
            year = year + 1;
        }
        doSearch();
    }

    @UiHandler("prev")
    public void prev(ClickEvent evt) {
        addMonth(-1);
    }

    public void doSearch() {
        if (year == 0) {
            year = JsDate.create().getFullYear();
        }
        if (month == 0) {
            month = JsDate.create().getMonth() + 1;
        }

        GWT.log("Searching: " + year + ", " + month);

        searchContainerInner.clear();
        searchProgress.setVisible(true);
        EloaaService.Instance.get().newReleases(year, month, new AsyncCallback<GResponse<List<GMovie>>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Search Failed: " + caught.getMessage());
            }

            @Override
            public void onSuccess(GResponse<List<GMovie>> result) {
                try {
                    searchProgress.setVisible(false);
                    if (result.isOK()) {
                        Date now = new Date();
                        int month=now.getMonth();
                        int day = now.getDate();
                        Widget w = null;
                        boolean scrolled=false;
                        for (GMovie m : result.get()) {
                            w = new MovieResult(m);
                            searchContainerInner.add(w);
                            if (!scrolled) {
                                if (m.getReleaseDate() != null) {
                                    if (m.getReleaseDate().getMonth() == month && Math.abs(m.getReleaseDate().getDate() - day) < 7) {
                                        w.getElement().scrollIntoView();
                                        scrolled=true;
                                    }
                                }
                            }
                        }
                        if (result.get().size() == 0) {
                            MaterialToast.fireToast("No new releases");
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    GWT.log("ERROR", t);
                }
            }
        });
    }
}
