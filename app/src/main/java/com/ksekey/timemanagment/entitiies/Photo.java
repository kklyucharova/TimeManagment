package com.ksekey.timemanagment.entitiies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

/**
 * Created by ikvant.
 */
@Entity
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int recordId;

    private String path;
    private String uri;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Uri getPhotoUri() {
        return Uri.parse(uri);
    }

    public void setPhotoUri(Uri uri) {
        this.uri = uri.toString();
    }
}
