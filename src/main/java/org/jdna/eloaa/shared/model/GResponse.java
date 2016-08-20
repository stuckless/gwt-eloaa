package org.jdna.eloaa.shared.model;

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
public class GResponse<T> implements IsSerializable {
    int code;
    String msg;
    T response;

    public GResponse() {
        this(Responses.OK,Responses.Msg.OK,null);
    }

    public GResponse(T resp) {
        this(Responses.OK,Responses.Msg.OK, resp);
    }

    public GResponse(String msg) {
        this(1, msg, null);
    }

    public GResponse(int code, String msg) {
        this(code, msg, null);
    }

    public GResponse(int code, String msg, T resp) {
        this.code=code;
        this.msg=msg;
        this.response=resp;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T get() {
        return response;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "GResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", response=" + response +
                '}';
    }

    public boolean isOK() {
        return code==Responses.OK;
    }
}
