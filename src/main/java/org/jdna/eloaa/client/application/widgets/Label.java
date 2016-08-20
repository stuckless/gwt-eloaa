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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by seans on 13/08/16.
 */
public class Label extends Composite {
    interface LabelUiBinder extends UiBinder<HTMLPanel, Label> {
    }

    private static LabelUiBinder ourUiBinder = GWT.create(LabelUiBinder.class);

    @UiField
    MaterialLabel label;

    @UiField
    MaterialLabel text;

    public Label() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setLabel(String text) {
        this.label.setText(text);
    }

    public void setText(String text) {
        this.text.setText(text);
        this.setVisible(text!=null && text.trim().length()>0);
    }
}