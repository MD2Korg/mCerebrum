package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinioBucket {

    @SerializedName("bucket-name")
    @Expose
    private Object bucketName;
    @SerializedName("last_modified")
    @Expose
    private Object lastModified;

    public Object getBucketName() {
        return bucketName;
    }

    public void setBucketName(Object bucketName) {
        this.bucketName = bucketName;
    }

    public Object getLastModified() {
        return lastModified;
    }

    public void setLastModified(Object lastModified) {
        this.lastModified = lastModified;
    }

}