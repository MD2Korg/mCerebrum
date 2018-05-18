package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinioObjectsListInBucket {

    @SerializedName("bucket-objects")
    @Expose
    private List<MinioObjectStats> bucketObjects = null;

    public List<MinioObjectStats> getBucketObjects() {
        return bucketObjects;
    }

    public void setBucketObjects(List<MinioObjectStats> bucketObjects) {
        this.bucketObjects = bucketObjects;
    }

}