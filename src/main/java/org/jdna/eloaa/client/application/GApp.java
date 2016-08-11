package org.jdna.eloaa.client.application;

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
import com.google.gwt.event.shared.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seans on 08/08/16.
 */
public class GApp {
    private static final GApp INSTANCE = new GApp();
    public static final GApp get() {
        return INSTANCE;
    }

    private List<HandlerRegistration> handlers = new ArrayList<>();

    private EventBus eventBus = GWT.create(SimpleEventBus.class);

    public EventBus getEventBus() {
        return eventBus;
    }

    public void reset() {
        for (HandlerRegistration r: handlers) {
            r.removeHandler();
        }
        handlers.clear();
    }

    public HandlerRegistration registerEventHander(GwtEvent.Type type, EventHandler handler) {
        HandlerRegistration reg = eventBus.addHandler(type, handler);
        handlers.add(reg);
        return reg;
    }
}
