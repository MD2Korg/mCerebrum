package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Properties {

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
    private List<DataDescriptor> dataDescriptor = null;
    @SerializedName("execution_context")
    @Expose
    private ExecutionContext executionContext;
    @SerializedName("annotations")
    @Expose
    private List<Annotation> annotations = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Properties() {
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
    public Properties(String identifier, String owner, String name, List<DataDescriptor> dataDescriptor, ExecutionContext executionContext, List<Annotation> annotations) {
        super();
        this.identifier = identifier;
        this.owner = owner;
        this.name = name;
        this.dataDescriptor = dataDescriptor;
        this.executionContext = executionContext;
        this.annotations = annotations;
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

    public List<DataDescriptor> getDataDescriptor() {
        return dataDescriptor;
    }

    public void setDataDescriptor(List<DataDescriptor> dataDescriptor) {
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