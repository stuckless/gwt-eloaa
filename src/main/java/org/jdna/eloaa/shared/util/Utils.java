package org.jdna.eloaa.shared.util;

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

import com.google.gwt.i18n.client.NumberFormat;
import org.jdna.eloaa.shared.model.GMovie;

/**
 * Created by seans on 08/08/16.
 */
public class Utils {
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return NumberFormat.getFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String formatPercent(double percent) {
        return NumberFormat.getFormat("##0.##").format(percent)+"%";
    }

    public static String toIMDBUrl(GMovie movie) {
        return toIMDBUrl(movie.getImdbid());
    }

    public static String toIMDBUrl(String id) {
        return "http://www.imdb.com/title/"+id+"/";
    }
}
