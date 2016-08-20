package org.jdna.eloaa.client.util;

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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialToast;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.application.event.MovieAdded;
import org.jdna.eloaa.shared.model.GMovie;
import org.jdna.eloaa.shared.model.GResponse;
import org.jdna.eloaa.shared.model.Responses;
import org.jdna.eloaa.client.service.EloaaService;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;
import org.jdna.eloaa.shared.util.Utils;

/**
 * Created by seans on 12/08/16.
 */
public class UIUtils {
    static final DateTimeFormat df = DateTimeFormat.getFormat("MMM dd, yyyy");
    public static final ClickHandler openIMDBHandler(final GMovie movie) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (movie.getImdbid()!=null) {
                    Window.open(Utils.toIMDBUrl(movie), "imdb", "");
                } else {
                    EloaaService.Instance.get().findIMDBID(movie.getId(), new AsyncCallback<GResponse<String>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(GResponse<String> result) {
                            if (result.isOK()) {
                                Window.open(Utils.toIMDBUrl(result.get()), "imdb", "");
                            }
                        }
                    });
                }
            }
        };
    }

    public static final void setOpenIMDBHandler(final GMovie movie, HasClickHandlers clickHandlers) {
        clickHandlers.addClickHandler(openIMDBHandler(movie));
        ((Widget)clickHandlers).getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    public static final ClickHandler openPreviewImage(final NzbItem nzbItem) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(getPreviewURL(nzbItem), "preview", "");
            }
        };
    }

    public static String getPreviewURL(NzbItem item) {
        return "https://nzbs.in/covers/preview/"+item.getGUID()+"_thumb.jpg";
    }

    public static String getFormattedReleaseDate(GMovie movie) {
        if (movie.getReleaseDate()!=null) {
            return df.format(movie.getReleaseDate());
        } else {
            return "";
        }
    }

    public static void addMovie(final GMovie movie) {
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
                    GApp.get().getEventBus().fireEvent(new MovieAdded(result.get()));
                }
            }
        });
    }
}
