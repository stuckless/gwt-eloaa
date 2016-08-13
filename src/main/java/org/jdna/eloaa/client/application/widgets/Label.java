package org.jdna.eloaa.client.application.widgets;

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