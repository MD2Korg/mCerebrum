package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class DataStream {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("data_descriptor")
    @Expose
    private List<HashMap<String, String>> dataDescriptor = null;
    @SerializedName("execution_context")
    @Expose
    private ExecutionContext executionContext;
    @SerializedName("annotations")
    @Expose
    private List<Annotation> annotations = null;
    @SerializedName("data")
    @Expose
    private List<DataPoints> dataPoints;
    /**
     * No args constructor for use in serialization
     *
     */
    public DataStream() {
    }

    /**
     *
     * @param dataDescriptor
     * @param name
     * @param owner
     * @param annotations
     * @param executionContext
     * @param identifier
     */
    public DataStream(String type, String identifier, String owner, String name, List<HashMap<String, String>> dataDescriptor, ExecutionContext executionContext, List<Annotation> annotations) {
        super();
        this.type = type;
        this.identifier = identifier;
        this.owner = owner;
        this.name = name;
        this.dataDescriptor = dataDescriptor;
        this.executionContext = executionContext;
        this.annotations = annotations;
    }

    public DataStream(String type, String identifier, String owner, String name, List<HashMap<String, String>> dataDescriptor, ExecutionContext executionContext, List<Annotation> annotations, List<DataPoints> dataPoints) {
        super();
        this.type = type;
        this.identifier = identifier;
        this.owner = owner;
        this.name = name;
        this.dataDescriptor = dataDescriptor;
        this.executionContext = executionContext;
        this.annotations = annotations;
        this.dataPoints = dataPoints;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HashMap<String, String>> getDataDescriptor() {
        return dataDescriptor;
    }

    public void setDataDescriptor(List<HashMap<String, String>> dataDescriptor) {
        this.dataDescriptor = dataDescriptor;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

}