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

/**
 * Created by seans on 08/08/16.
 */
public class Responses {
    // codes
    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int ERR_SQL = 2;
    public static final int ERR_ALREADY_EXISTS = 3;

    public static final class Msg {
        public static final String OK = "OK";
        public static final String ERROR = "ERROR";
    }
}
