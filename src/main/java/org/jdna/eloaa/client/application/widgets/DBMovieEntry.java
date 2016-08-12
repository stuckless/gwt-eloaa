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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.client.base.AbstractIconButton;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.application.event.MovieNZBSearch;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.model.GProgress;
import org.jdna.eloaa.client.model.GResponse;
import org.jdna.eloaa.client.service.EloaaService;
import org.jdna.eloaa.client.util.UIUtils;
import org.jdna.eloaa.shared.util.Utils;

/**
 * Created by seans on 08/08/16.
 */
public class DBMovieEntry extends Composite {
    interface DBMovieEntryUiBinder extends UiBinder<MaterialCollapsibleItem, DBMovieEntry> {
    }

    private static DBMovieEntryUiBinder ourUiBinder = GWT.create(DBMovieEntryUiBinder.class);

    private GMovie movie;

    @UiField
    MaterialLink title;

    @UiField
    MaterialLink btnSearch;

    @UiField
    MaterialCollapsibleItem item;

    @UiField
    MaterialProgress progress;

    @UiField
    MaterialTooltip progressTooltip;

    @UiField
    MaterialLabel status;

    @UiField
    MaterialLabel statusMessage;

    @UiField
    MaterialLabel releaseDate;

    @UiField
    AbstractIconButton btnDelete;

    @UiField
    AbstractIconButton btnRemove;

    @UiField
    AbstractIconButton trailer;

    public DBMovieEntry(GMovie movie) {
        initWidget(ourUiBinder.createAndBindUi(this));
        trailer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openTrailer();
            }
        });
        setMovie(movie);
    }

    private void openTrailer() {
        UIUtils.openIMDBHandler(movie).onClick(null);
    }

    @UiHandler("btnSearch")
    public void onSearch(ClickEvent evt) {
        evt.stopPropagation();

        GApp.get().getEventBus().fireEvent(new MovieNZBSearch(movie));
    }

    public void setMovie(GMovie movie) {
        this.movie=movie;
        setupProgressWatcher();
        updateUI();
    }

    private void updateUI() {
        title.setText(movie.getFullTitle());
        status.setText(movie.getStatus());
        statusMessage.setText(movie.getStatusMessage());
        releaseDate.setText(UIUtils.getFormattedReleaseDate(movie));

        if (movie.isComplete()) {
            title.setIconColor("green");
            btnSearch.setVisible(false);
            progress.setVisible(false);
            progress.setPercent(100.0);
            unmonitor();
            return;
        }

        if (GProgress.STATUS_MOVING.equalsIgnoreCase(movie.getStatus())) {
            title.setIconColor("purple");
            btnSearch.setVisible(true);
            progress.setVisible(false);
            return;
        }

        if (GProgress.STATUS_ERROR.equalsIgnoreCase(movie.getStatus())) {
            title.setIconColor("red");
            btnSearch.setVisible(true);
            progress.setVisible(false);
            unmonitor();
            return;
        }

        if (movie.isDownloaded()) {
            title.setIconColor("purple");
            btnSearch.setVisible(false);
            progress.setVisible(false);
            progress.setType(ProgressType.DETERMINATE);
            progress.setPercent(100.0);
            return;
        }

        if (movie.getDownloadToken()==null) {
            title.setIconColor("orange");
            btnSearch.setVisible(true);
            progress.setVisible(false);
        }

        if (movie.getDownloadToken()!=null) {
            title.setIconColor("blue");
            btnSearch.setVisible(false);
            progress.setVisible(true);
            if (movie.getBytesDownloaded()>0) {
                progress.setType(ProgressType.DETERMINATE);
            } else {
                progress.setType(ProgressType.INDETERMINATE);
            }
        }
    }

    Timer progressWatcher;
    private void setupProgressWatcher() {
        if (progressWatcher!=null) {
            // we already have a progress monitor, just exit;
            return;
        }
        progressWatcher = new Timer() {
            @Override
            public void run() {
                EloaaService.Instance.get().getDownloadMovieProgress(movie.getId(), new AsyncCallback<GResponse<GProgress>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        // TODO: what to do??
                    }

                    @Override
                    public void onSuccess(GResponse<GProgress> result) {
                        GWT.log("PROGRESS: " + result);
                        if (result.isOK()) {
                            GProgress p = result.get();
                            if (p.isComplete()) {
                                movie.setDownloaded(true);
                                updateUI();
                                return;
                            }

                            if (p.getPercent()>0) {
                                progress.setType(ProgressType.DETERMINATE);
                                progress.setPercent(p.getPercent());

                                progressTooltip.setText(""+Utils.formatPercent(p.getPercent()) + " of " + Utils.readableFileSize(p.getMax()));

                                movie.setSize(p.getMax());
                                movie.setBytesDownloaded(p.getProgress());
                                movie.setStatus(p.getStatus());
                                movie.setStatusMessage(p.getStatusMessage());
                                if (p.isComplete()) {
                                    movie.setDownloaded(true);
                                }

                                updateUI();
                            } else {
                                // leave it as indeterminant
                            }


                        } else {
                            // TODO: what to do??
                        }
                    }
                });
            }
        };
        progressWatcher.scheduleRepeating(5000);
    }

    public GMovie getMovie() {
        return movie;
    }

    @Override
    protected void onDetach() {
        unmonitor();
        super.onDetach();
    }

    private void unmonitor() {
        if (progressWatcher!=null) {
            progressWatcher.cancel();
            progressWatcher = null;
        }
    }

    @UiHandler("btnDelete")
    public void onDelete(ClickEvent evt) {
        EloaaService.Instance.get().deleteMovie(movie, new AsyncCallback<GResponse<Boolean>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failed to delete movie");
            }

            @Override
            public void onSuccess(GResponse<Boolean> result) {
                if (result.get()) {
                    DBMovieEntry.this.removeFromParent();
                    MaterialToast.fireToast("Deleted " + movie.getFullTitle());
                } else {
                    MaterialToast.fireToast("Failed to delete movie");
                }
            }
        });
    }

    @UiHandler("btnRemove")
    public void onRemove(ClickEvent evt) {
        EloaaService.Instance.get().removeMovie(movie, new AsyncCallback<GResponse<Boolean>>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failed to remove movie");
            }

            @Override
            public void onSuccess(GResponse<Boolean> result) {
                if (result.get()) {
                    DBMovieEntry.this.removeFromParent();
                    MaterialToast.fireToast("Removed " + movie.getFullTitle());
                } else {
                    MaterialToast.fireToast("Failed to remove movie");
                }
            }
        });
    }
}