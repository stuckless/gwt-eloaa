package org.jdna.newznab.api.model;

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

import org.simpleframework.xml.*;

import java.util.List;

/**
 * Created by seans on 03/08/16.
 */
@Root(name="rss", strict = false)
public class SearchResults {
    @Attribute
    @Path("channel/response")
    @Namespace(reference="http://www.newznab.com/DTD/2010/feeds/attributes/", prefix="newznab")
    private int offset;

    @Attribute
    @Path("channel/response")
    @Namespace(reference="http://www.newznab.com/DTD/2010/feeds/attributes/", prefix="newznab")
    private int total;

    @ElementList(inline = true)
    @Path("channel")
    private List<SearchResultItem> items;

    public int getTotal() {
        return total;
    }

    public int getOffset() {
        return offset;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SearchResults{" +
                "offset=" + offset +
                ", total=" + total +
                ", items=" + items +
                '}';
    }
}
