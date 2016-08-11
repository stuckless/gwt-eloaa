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
import gwt.material.design.client.base.SearchObject;

import java.util.Date;

/**
 * Created by seans on 04/08/16.
 */
public class GMovie extends SearchObject implements IsSerializable {
    String id;
    String imdbid;
    String title;
    String posterUrl;
    String year;
    String description;

    Date dateAdded;
    long size;
    long bytesDownloaded;
    boolean downloaded;
    String localFile;
    String downloadToken;
    private String status;
    private String statusMessage;

    public GMovie() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getFullTitle() {
        return title + ((year==null)?"":(" ("+year+")"));
    }

    public String getImdbid() {
        return imdbid;
    }

    public void setImdbid(String imdbid) {
        this.imdbid = imdbid;
    }

    public boolean isDownloaded() {
        return downloaded || GProgress.STATUS_DOWNLOADED.equalsIgnoreCase(status);
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GMovie{" +
                "id='" + id + '\'' +
                ", imdbid='" + imdbid + '\'' +
                ", title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", year='" + year + '\'' +
                ", description='" + description + '\'' +
                ", dateAdded=" + dateAdded +
                ", size=" + size +
                ", bytesDownloaded=" + bytesDownloaded +
                ", downloaded=" + downloaded +
                ", localFile='" + localFile + '\'' +
                ", downloadToken='" + downloadToken + '\'' +
                ", status='" + status + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }


    public boolean isComplete() {
        return isDownloaded() && GProgress.STATUS_COMPLETE.equalsIgnoreCase(status);
    }
}
