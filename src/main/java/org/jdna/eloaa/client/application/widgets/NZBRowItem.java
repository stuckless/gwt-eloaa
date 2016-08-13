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
import gwt.material.design.client.base.AbstractIconButton;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;
import org.jdna.eloaa.client.application.GApp;
import org.jdna.eloaa.client.application.event.DownloadMovie;
import org.jdna.eloaa.client.model.GMovie;
import org.jdna.eloaa.client.util.UIUtils;
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

/**
 * Created by seans on 08/08/16.
 */
public class NZBRowItem extends Composite {
    private final NzbItem item;
    private final GMovie movie;

    interface NZBRowItemUiBinder extends UiBinder<MaterialRow, NZBRowItem> {
    }

    private static NZBRowItemUiBinder ourUiBinder = GWT.create(NZBRowItemUiBinder.class);

    @UiField
    MaterialLink title;

    @UiField
    MaterialLabel size;

    @UiField
    MaterialLabel age;

    @UiField
    MaterialIcon download;

    public NZBRowItem(NzbItem item, GMovie movie) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.item=item;
        this.movie=movie;
        this.title.setText(item.getTitle());
        this.title.addClickHandler(UIUtils.openPreviewImage(item));
        this.size.setText(org.jdna.eloaa.shared.util.Utils.readableFileSize(item.getSize()));
        this.size.setTextAlign(TextAlign.RIGHT);
        this.age.setText(item.getAge()+"d");
        this.age.setTextAlign(TextAlign.RIGHT);
    }

    @UiHandler("download")
    public void onDownload(ClickEvent event) {
        GApp.get().getEventBus().fireEvent(new DownloadMovie(item, movie));
    }
}