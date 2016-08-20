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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by seans on 06/08/16.
 */
@DatabaseTable
public class MovieEntry implements Serializable {
    @DatabaseField(id = true)
    private String id;

    @DatabaseField(index = true)
    private String imdbID;

    @DatabaseField(index = true)
    private Date dateAdded;

    @DatabaseField(index = true)
    private Date releaseDate;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField
    private String year;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String description;

    @DatabaseField
    private String posterURL;

    @DatabaseField(index = true)
    private String localFile;

    @DatabaseField
    private long size;

    @DatabaseField
    private long bytesDownloaded;

    @DatabaseField (index = true)
    private boolean downloaded;

    @DatabaseField(index = true)
    private String downloadToken;

    @DatabaseField(index = true)
    private String status;

    @DatabaseField(index = true)
    private String statusMessage;

    public MovieEntry() {
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
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

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
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

    public boolean isDownloaded() {
        return downloaded || GProgress.STATUS_DOWNLOADED.equalsIgnoreCase(status);
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isComplete() {
        return isDownloaded() && GProgress.STATUS_COMPLETE.equalsIgnoreCase(status);
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }


    @Override
    public String toString() {
        return "MovieEntry{" +
                "id='" + id + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", dateAdded=" + dateAdded +
                ", releaseDate=" + releaseDate +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", description='" + description + '\'' +
                ", posterURL='" + posterURL + '\'' +
                ", localFile='" + localFile + '\'' +
                ", size=" + size +
                ", bytesDownloaded=" + bytesDownloaded +
                ", downloaded=" + downloaded +
                ", downloadToken='" + downloadToken + '\'' +
                ", status='" + status + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }

    public GMovie toGMovie() {
        GMovie m = new GMovie();
        m.setId(getId());
        m.setImdbid(getImdbID());
        m.setPosterUrl(getPosterURL());
        m.setDescription(getDescription());
        m.setTitle(getTitle());
        m.setYear(getYear());
        m.setDateAdded(getDateAdded());
        m.setDownloaded(isDownloaded());
        m.setDownloadToken(getDownloadToken());
        m.setStatus(getStatus());
        m.setStatusMessage(getStatusMessage());
        m.setReleaseDate(getReleaseDate());
        return m;
    }

    public boolean isPaused() {
        return status!=null && status.contains("pause");
    }

    public boolean isMoving() {
        return GProgress.STATUS_MOVING.equalsIgnoreCase(status);
    }

    public boolean isError() {
        return GProgress.STATUS_ERROR.equalsIgnoreCase(status);
    }
}
