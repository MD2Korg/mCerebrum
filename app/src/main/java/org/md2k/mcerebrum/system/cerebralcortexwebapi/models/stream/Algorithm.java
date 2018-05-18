package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Algorithm {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("authors")
    @Expose
    private List<String> authors = null;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("reference")
    @Expose
    private Reference reference;

    /**
     * No args constructor for use in serialization
     *
     */
    public Algorithm() {
    }

    /**
     *
     * @param authors
     * @param description
     * @param method
     * @param reference
     * @param version
     */
    public Algorithm(String method, String description, List<String> authors, String version, Reference reference) {
        super();
        this.method = method;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.reference = reference;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

}