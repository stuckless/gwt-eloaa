package org.jdna.sabnzbd.api.model;

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

import java.io.Serializable;
import java.util.List;

/**
 * Created by seans on 08/08/16.
 */
public class Queue implements Serializable {
    public String speed;
    public String size;
    public String last_warning;
    public String have_warnings;
    public List<Slot> slots;
    public Queue() {
    }

    @Override
    public String toString() {
        return "Queue{" +
                "speed='" + speed + '\'' +
                ", size='" + size + '\'' +
                ", last_warning='" + last_warning + '\'' +
                ", have_warnings='" + have_warnings + '\'' +
                ", slots=" + slots +
                '}';
    }
}
