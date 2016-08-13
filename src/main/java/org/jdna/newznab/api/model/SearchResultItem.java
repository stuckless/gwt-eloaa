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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seans on 03/08/16.
 */
@Root(name = "item", strict = false)
public class SearchResultItem {
    @Element
    String title;

    @Element
    String pubDate;

    @Element
    String category;

    @Element
    String description;

    @ElementMap(entry = "attr", key = "name", value="value", attribute = true, inline = true)
    @Namespace(reference="http://www.newznab.com/DTD/2010/feeds/attributes/", prefix="newznab")
    Map<String,String> attributes = new HashMap<>();

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public long getSize() {
        return getLongAttr("size");
    }

    public String getCategoryCode() {
        return getAttr("category");
    }

    public String getGroup() {
        return getAttr("group");
    }

    public String getPoster() {
        return getAttr("poster");
    }

    public String getUsenetDate() {
        return getAttr("usenetdate");
    }

    public String getGUID() {
        return getAttr("guid");
    }



    private long getLongAttr(String attr) {
        Object val = attributes.get(attr);
        if (val==null) return 0;
        return Long.parseLong(val.toString());
    }

    private String getAttr(String attr) {
        Object val = attributes.get(attr);
        if (val==null) return null;
        return val.toString();
    }

    @Override
    public String toString() {
        return "SearchResultItem{" +
                "title='" + title + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
