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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seans on 03/08/16.
 */
@Root(name = "caps", strict = false)
public class Capabilities {
    @Attribute(name="version")
    @Path("server")
    String serverVersion;

    @ElementList(name = "categories")
    public List<Category> categories = new ArrayList<>();

    public List<Category> getCategories() {
        return categories;
    }
    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public String toString() {
        return "Capabilities{" +
                "categories=" + categories +
                '}';
    }
}
