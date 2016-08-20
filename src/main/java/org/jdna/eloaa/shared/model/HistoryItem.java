package org.jdna.eloaa.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by seans on 18/08/16.
 */
@DatabaseTable
public class HistoryItem implements IsSerializable {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(index = true)

    private String movie_id;

    @DatabaseField
    private String status;

    @DatabaseField()
    private String message;


    public HistoryItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
