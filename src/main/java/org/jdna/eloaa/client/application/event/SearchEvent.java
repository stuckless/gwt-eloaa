package org.jdna.eloaa.client.application.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by seans on 11/08/16.
 */
public class SearchEvent extends GwtEvent<SearchEventHandler> {
    public static Type<SearchEventHandler> TYPE = new Type<SearchEventHandler>();
    private final boolean enableSearch;
    private final String text;

    public Type<SearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    public SearchEvent(boolean enableSearch, String text) {
        this.enableSearch=enableSearch;
        this.text=text;
    }

    public boolean isEnableSearch() {
        return enableSearch;
    }

    public String getText() {
        return text;
    }

    protected void dispatch(SearchEventHandler handler) {
        handler.onSearch(this);
    }
}
