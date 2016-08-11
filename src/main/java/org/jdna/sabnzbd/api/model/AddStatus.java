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

import java.util.List;

/**
 * Created by seans on 08/08/16.
 */
public class AddStatus {
    public boolean status;
    public String error;

    public List<String> nzo_ids;

    public AddStatus() {
    }

    public String getDownloadToken() {
        if (nzo_ids==null || nzo_ids.size()==0) {
            return null;
        }

        if (nzo_ids.size()==1) {
            return nzo_ids.get(0);
        }

        return join(nzo_ids, ",");
    }

    public static String join(List<?> list, String delim) {
        int len = list.size();
        if (len == 0)
            return "";
        StringBuilder sb = new StringBuilder(list.get(0).toString());
        for (int i = 1; i < len; i++) {
            sb.append(delim);
            sb.append(list.get(i).toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AddStatus{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", nzo_ids=" + nzo_ids +
                '}';
    }
}
