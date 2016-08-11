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
import org.jdna.eloaa.client.model.GMovie;

/**
 * Created by seans on 08/08/16.
 */
public class MovieAdded extends GwtEvent<MovieAddedHandler> {
    public static Type<MovieAddedHandler> TYPE = new Type<MovieAddedHandler>();
    private final GMovie movie;

    public Type<MovieAddedHandler> getAssociatedType() {
        return TYPE;
    }

    public MovieAdded(GMovie movie) {
        this.movie=movie;
    }

    protected void dispatch(MovieAddedHandler handler) {
        handler.onMovieAdded(this);
    }

    public GMovie getMovie() {
        return movie;
    }
}
