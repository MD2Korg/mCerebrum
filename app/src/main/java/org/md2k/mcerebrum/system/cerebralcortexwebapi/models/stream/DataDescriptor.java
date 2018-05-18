package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataDescriptor {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("unit")
    @Expose
    private String unit;

    /**
     * No args constructor for use in serialization
     *
     */
    public DataDescriptor() {
    }

    /**
     *
     * @param unit
     * @param type
     */
    public DataDescriptor(String type, String unit) {
        super();
        this.type = type;
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}