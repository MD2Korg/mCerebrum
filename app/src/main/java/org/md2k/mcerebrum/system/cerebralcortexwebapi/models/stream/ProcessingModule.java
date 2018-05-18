package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProcessingModule {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("input_parameters")
    @Expose
    private InputParameters inputParameters;
    @SerializedName("input_streams")
    @Expose
    private List<InputStream> inputStreams = null;
    @SerializedName("output_streams")
    @Expose
    private List<OutputStream> outputStreams = null;
    @SerializedName("algorithm")
    @Expose
    private List<Algorithm> algorithm = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public ProcessingModule() {
    }

    /**
     *
     * @param outputStreams
     * @param inputParameters
     * @param inputStreams
     * @param description
     * @param name
     * @param algorithm
     */
    public ProcessingModule(String name, String description, InputParameters inputParameters, List<InputStream> inputStreams, List<OutputStream> outputStreams, List<Algorithm> algorithm) {
        super();
        this.name = name;
        this.description = description;
        this.inputParameters = inputParameters;
        this.inputStreams = inputStreams;
        this.outputStreams = outputStreams;
        this.algorithm = algorithm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InputParameters getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(InputParameters inputParameters) {
        this.inputParameters = inputParameters;
    }

    public List<InputStream> getInputStreams() {
        return inputStreams;
    }

    public void setInputStreams(List<InputStream> inputStreams) {
        this.inputStreams = inputStreams;
    }

    public List<OutputStream> getOutputStreams() {
        return outputStreams;
    }

    public void setOutputStreams(List<OutputStream> outputStreams) {
        this.outputStreams = outputStreams;
    }

    public List<Algorithm> getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(List<Algorithm> algorithm) {
        this.algorithm = algorithm;
    }

}