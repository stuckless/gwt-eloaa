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
import org.jdna.eloaa.shared.nzbs.model.NzbItem;

/**
 * Created by seans on 08/08/16.
 */
public class DownloadMovie extends GwtEvent<DownloadMovieHandler> {
    public static Type<DownloadMovieHandler> TYPE = new Type<DownloadMovieHandler>();
    private final NzbItem item;
    private final GMovie movie;

    public Type<DownloadMovieHandler> getAssociatedType() {
        return TYPE;
    }

    public DownloadMovie(NzbItem item, GMovie movie) {
        this.item=item;
        this.movie=movie;
    }

    protected void dispatch(DownloadMovieHandler handler) {
        handler.onDownloadMovie(this);
    }

    public NzbItem getItem() {
        return item;
    }

    public GMovie getMovie() {
        return movie;
    }
}
