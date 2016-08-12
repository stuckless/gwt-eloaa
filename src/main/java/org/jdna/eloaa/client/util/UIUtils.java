package org.jdna.eloaa.client.util;

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
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.model.Responses;
import org.jdna.eloaa.client.service.EloaaService;
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
