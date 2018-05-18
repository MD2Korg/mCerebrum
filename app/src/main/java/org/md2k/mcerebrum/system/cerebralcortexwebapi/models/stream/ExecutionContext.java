package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ExecutionContext {

    @SerializedName("processing_module")
    @Expose
    private ProcessingModule processingModule;

    @SerializedName("datasource_metadata")
    @Expose
    private HashMap<String, String> datasource_metadata;
    @SerializedName("application_metadata")
    @Expose
    private HashMap<String, String> application_metadata;
    @SerializedName("platform_metadata")
    @Expose
    private HashMap<String, String> platform_metadata;
    @SerializedName("platformapp_metadata")
    @Expose
    private HashMap<String, String> platformapp_metadata;
    /**
     * No args constructor for use in serialization
     *
     */
    public ExecutionContext() {
    }

    /**
     *
     * @param processingModule
     */
    public ExecutionContext(ProcessingModule processingModule, HashMap<String, String> datasource_metadata, HashMap<String, String> application_metadata, HashMap<String, String> platform_metadata, HashMap<String, String> platformapp_metadata) {
        super();
        this.processingModule = processingModule;
        this.datasource_metadata = datasource_metadata;
        this.application_metadata = application_metadata;
        this.platform_metadata = platform_metadata;
        this.platformapp_metadata = platformapp_metadata;


    }

    public ProcessingModule getProcessingModule() {
        return processingModule;
    }

    public void setProcessingModule(ProcessingModule processingModule) {
        this.processingModule = processingModule;
    }

    public HashMap<String, String> getDatasource_metadata() {
        return datasource_metadata;
    }

    public void setDatasource_metadata(HashMap<String, String> datasource_metadata) {
        this.datasource_metadata = datasource_metadata;
    }

    public HashMap<String, String> getPlatformapp_metadata() {
        return platformapp_metadata;
    }

    public void setPlatformapp_metadata(HashMap<String, String> platformapp_metadata) {
        this.platformapp_metadata = platformapp_metadata;
    }

    public HashMap<String, String> getPlatform_metadata() {
        return platform_metadata;
    }

    public void setPlatform_metadata(HashMap<String, String> platform_metadata) {
        this.platform_metadata = platform_metadata;
    }

    public HashMap<String, String> getApplication_metadata() {
        return application_metadata;
    }

    public void setApplication_metadata(HashMap<String, String> application_metadata) {
        this.application_metadata = application_metadata;
    }
}