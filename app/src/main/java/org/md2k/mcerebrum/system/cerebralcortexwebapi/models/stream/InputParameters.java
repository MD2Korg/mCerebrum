package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputParameters {

    @SerializedName("window_size")
    @Expose
    private Integer windowSize;
    @SerializedName("window_offset")
    @Expose
    private Integer windowOffset;
    @SerializedName("low_level_threshold")
    @Expose
    private Double lowLevelThreshold;
    @SerializedName("high_level_threshold")
    @Expose
    private Double highLevelThreshold;

    /**
     * No args constructor for use in serialization
     *
     */
    public InputParameters() {
    }

    /**
     *
     * @param windowOffset
     * @param highLevelThreshold
     * @param lowLevelThreshold
     * @param windowSize
     */
    public InputParameters(Integer windowSize, Integer windowOffset, Double lowLevelThreshold, Double highLevelThreshold) {
        super();
        this.windowSize = windowSize;
        this.windowOffset = windowOffset;
        this.lowLevelThreshold = lowLevelThreshold;
        this.highLevelThreshold = highLevelThreshold;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Integer getWindowOffset() {
        return windowOffset;
    }

    public void setWindowOffset(Integer windowOffset) {
        this.windowOffset = windowOffset;
    }

    public Double getLowLevelThreshold() {
        return lowLevelThreshold;
    }

    public void setLowLevelThreshold(Double lowLevelThreshold) {
        this.lowLevelThreshold = lowLevelThreshold;
    }

    public Double getHighLevelThreshold() {
        return highLevelThreshold;
    }

    public void setHighLevelThreshold(Double highLevelThreshold) {
        this.highLevelThreshold = highLevelThreshold;
    }

}