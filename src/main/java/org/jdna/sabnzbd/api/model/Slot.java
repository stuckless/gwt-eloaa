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

/**
 * Created by seans on 08/08/16.
 */
public class Slot implements Serializable {
    public String status;
    public int index;
    public int missing;
    public String mb;
    public String sizeleft;
    public String filename;
    public String priority;
    public String cat;
    public String mbleft;
    public String eta;
    public String timeleft;
    public String percentage;
    public String nzo_id;
    public String size;
    public long downloaded;
    public long bytes;
    public String storage;

    public Slot() {
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    public boolean isPaused() {
        return status!=null && status.toLowerCase().contains("pause");
    }

    public boolean hasError() {
        return status!=null && status.toLowerCase().contains("error");
    }

    @Override
    public String toString() {
        return "Slot{" +
                "status='" + status + '\'' +
                ", index=" + index +
                ", missing=" + missing +
                ", mb='" + mb + '\'' +
                ", sizeleft='" + sizeleft + '\'' +
                ", filename='" + filename + '\'' +
                ", priority='" + priority + '\'' +
                ", cat='" + cat + '\'' +
                ", mbleft='" + mbleft + '\'' +
                ", eta='" + eta + '\'' +
                ", timeleft='" + timeleft + '\'' +
                ", percentage='" + percentage + '\'' +
                ", nzo_id='" + nzo_id + '\'' +
                ", size='" + size + '\'' +
                ", downloaded=" + downloaded +
                ", bytes=" + bytes +
                ", storage='" + storage + '\'' +
                '}';
    }
}
