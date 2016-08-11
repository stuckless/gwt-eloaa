package org.jdna.eloaa.client.application.event;

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
