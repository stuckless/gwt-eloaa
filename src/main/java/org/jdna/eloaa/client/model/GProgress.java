package org.jdna.eloaa.client.model;

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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by seans on 08/08/16.
 */
public class GProgress implements IsSerializable {
    /** status is not known, yet, not started, etc */
    public static final String STATUS_NOT_STARTED = "not_started";
    /** file is downloaded, but still required post processing */
    public static final String STATUS_DOWNLOADED = "downloaded";
    /** file is being downloaded */
    public static final String STATUS_IN_PROGRESS = "in_progress";
    /** movie is downloaded and renamed into destination directory */
    public static final String STATUS_COMPLETE = "complete";
    public static final String STATUS_PAUSED = "paused";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_MOVING = "moving";

    String id;
    long progress;
    long max;
    private String status;

    @Override
    public String toString() {
        return "GProgress{" +
                "id='" + id + '\'' +
                ", progress=" + progress +
                ", max=" + max +
                ", status='" + status + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    private String statusMessage;

    public GProgress() {
    }

    public GProgress(String id, long max, long progress) {
        this.id = id;
        this.progress = progress;
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public double getPercent() {
        return (((double)progress/(double)max) * 100d);
    }

    public boolean isComplete() {
        return progress>0 && max>0 && progress>=max;
    }

    public String getStatus() {
        return status;
    }

    public GProgress status(String status) {
        this.status=status;
        return this;
    }

    public GProgress statusMessage(String statusMessage) {
        this.statusMessage=statusMessage;
        return this;
    }
}
