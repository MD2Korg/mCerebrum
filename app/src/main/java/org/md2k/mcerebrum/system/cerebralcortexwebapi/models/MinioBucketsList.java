package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ali on 9/20/17.
 */

public class MinioBucketsList {
    @SerializedName("buckets-list")
    @Expose
    private List<MinioBucket> minioBuckets = null;

    public List<MinioBucket> getMinioBuckets() {
        return minioBuckets;
    }

    public void setMinioBuckets(List<MinioBucket> minioBuckets) {
        this.minioBuckets = minioBuckets;
    }

}