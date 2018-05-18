package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinioObjectStats {
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("is_dir")
    @Expose
    private String isDir;
    @SerializedName("content_type")
    @Expose
    private String contentType;
    @SerializedName("bucket_name")
    @Expose
    private String bucketName;
    @SerializedName("object_name")
    @Expose
    private String objectName;
    @SerializedName("last_modified")
    @Expose
    private String lastModified;
    @SerializedName("size")
    @Expose
    private String size;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getIsDir() {
        return isDir;
    }

    public void setIsDir(String isDir) {
        this.isDir = isDir;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
