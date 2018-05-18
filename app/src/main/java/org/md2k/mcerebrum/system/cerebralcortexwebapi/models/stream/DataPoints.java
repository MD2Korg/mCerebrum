package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataPoints {

    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("endtime")
    @Expose
    private String endtime;
    @SerializedName("sample")
    @Expose
    private String sample;

    public DataPoints() {
    }

    /**
     *
     * @param endtime
     * @param starttime
     * @param sample
     */
    public DataPoints(String starttime, String endtime, String sample) {
        super();
        this.starttime = starttime;
        this.endtime = endtime;
        this.sample = sample;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

}