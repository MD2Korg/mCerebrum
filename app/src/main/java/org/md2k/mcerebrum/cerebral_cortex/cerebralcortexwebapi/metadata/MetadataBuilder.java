package org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.metadata;

import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.Algorithm;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.Annotation;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.DataStream;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.ExecutionContext;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.InputParameters;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.InputStream;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.OutputStream;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.ProcessingModule;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.Reference;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MetadataBuilder {
    public DataStream buildDataStreamMetadata(String userUUID, DataSourceClient dsc) {

        //From DataKit
        int datasource_identifier = dsc.getDs_id();

        List<HashMap<String, String>> datasource_dataDescriptors = dsc.getDataSource().getDataDescriptors();
        if (datasource_dataDescriptors == null) {
            datasource_dataDescriptors = new ArrayList<HashMap<String, String>>();
        }
        String datasource_id = dsc.getDataSource().getId();
        String datasource_type = dsc.getDataSource().getType();
        HashMap<String, String> datasource_metadata = dsc.getDataSource().getMetadata();

        String application_id = null;
        String application_type = null;
        HashMap<String, String> application_metadata = null;
        if (dsc.getDataSource().getApplication() != null) {
            application_id = dsc.getDataSource().getApplication().getId();
            application_type = dsc.getDataSource().getApplication().getType();
            application_metadata = dsc.getDataSource().getApplication().getMetadata();
        }
        String platform_id = null;
        String platform_type = null;
        HashMap<String, String> platform_metadata = null;
        if (dsc.getDataSource().getPlatform() != null) {
            platform_id = dsc.getDataSource().getPlatform().getId();
            platform_type = dsc.getDataSource().getPlatform().getType();
            platform_metadata = dsc.getDataSource().getPlatform().getMetadata();
        }
        String platformapp_id = null;
        String platformapp_type = null;
        HashMap<String, String> platformapp_metadata = null;
        if (dsc.getDataSource().getPlatformApp() != null) {
            platformapp_id = dsc.getDataSource().getPlatformApp().getId();
            platformapp_type = dsc.getDataSource().getPlatformApp().getType();
            platformapp_metadata = dsc.getDataSource().getPlatformApp().getMetadata();
        }
        UUID ownerUUID = UUID.fromString(userUUID);
        String stream = userUUID.toString() + generateDSCString(dsc);
        UUID streamUUID = UUID.nameUUIDFromBytes(stream.getBytes());

        String streamName = "";
        List<String> nameComponents = new ArrayList<>();
        nameComponents.add(datasource_type);
        nameComponents.add(datasource_id);
        nameComponents.add(application_type);
        nameComponents.add(application_id);
        nameComponents.add(platform_type);
        nameComponents.add(platform_id);
        nameComponents.add(platformapp_id);
        nameComponents.add(platformapp_type);
        nameComponents.removeAll(Collections.singleton(null));

        streamName = nameComponents.get(0);
        nameComponents.remove(0);
        for (String s : nameComponents) {
            streamName += "--" + s;
        }

        //Algorithm
        String algoMethod = "";
        String algoDescription = "";
        String algoAuthorName = "";
        List<String> authors = new ArrayList<String>();
        authors.add(algoAuthorName);
        String algoVersion = "";

        //Reference meta
        String referenceUrl = "http://md2k.org/";

        //Annotations meta
        String annotationName = "";
        String annotationIdentifier = "";

        //DataDescriptor meta
        String dataType = "";
        String dataUnit = "";

        //InputParameters meta
//        Integer windowSize = 60;
//        Integer windowOffset = 300;
//        Double lowLevelThreshold = 1.1;
//        Double highLevelThreshold = 1.4;

        //InputStream meta
        String inputStreamName = "";
        String inputStreamIdentifier = "";

        //outputStream meta
        String outputStreamName = "";
        String outputStreamIdentifier = "";

        //ProcessingModule meta
        String processingModuleName = "";
        String processingModuleDescription = "";


        InputStream inputStream = new InputStream(inputStreamName, inputStreamIdentifier);

        OutputStream outputStream = new OutputStream(outputStreamName, outputStreamIdentifier);
//        InputParameters inputParameters = new InputParameters(windowSize, windowOffset, lowLevelThreshold, highLevelThreshold);
        InputParameters inputParameters = new InputParameters();
        Annotation annotation = new Annotation(annotationName, annotationIdentifier);
        Reference reference = new Reference(referenceUrl);
        Algorithm algorithm = new Algorithm(algoMethod, algoDescription, authors, algoVersion, reference);

        List<InputStream> inputStreams = new ArrayList<InputStream>();

        List<OutputStream> outputStreams = new ArrayList<OutputStream>();

        List<Algorithm> algorithms = new ArrayList<Algorithm>();
        algorithms.add(algorithm);


        List<Annotation> annotations = new ArrayList<Annotation>();

        ProcessingModule processingModule = new ProcessingModule(processingModuleName, processingModuleDescription, inputParameters, inputStreams, outputStreams, algorithms);
        ExecutionContext executionContext = new ExecutionContext(processingModule, datasource_metadata, application_metadata, platform_metadata, platformapp_metadata);

//        if(rawOrZip=="zip") {
        DataStream dataStream = new DataStream("datastream", streamUUID.toString(), ownerUUID.toString(), streamName, datasource_dataDescriptors, executionContext, annotations);
        return dataStream;
//        }else{
//            DataPoints dataPoints = new DataPoints("12345", "156789", "0101");
//            List<DataPoints> dataPointsList = new ArrayList<DataPoints>();
//            dataPointsList.add(dataPoints);
//            DataStream dataStream = new DataStream(type, identifier, owner, name, dataDescriptors, executionContext, annotations, dataPointsList);
//            return dataStream;
//        }

    }

    private String generateDSCString(DataSourceClient dsc) {
        String result = "";

        List<HashMap<String, String>> datasource_dataDescriptors = dsc.getDataSource().getDataDescriptors();
        if (datasource_dataDescriptors != null) {
            for (Map<String, String> map : datasource_dataDescriptors) {
                if (map != null) {
                    for (Map.Entry<String, String> meta : map.entrySet()) {
                        result += meta.getKey();
                        result += meta.getValue();
                    }
                }
            }
        }

        result += dsc.getDataSource().getId();
        result += dsc.getDataSource().getType();
        if (dsc.getDataSource().getMetadata() != null) {
            for (Map.Entry<String, String> meta : dsc.getDataSource().getMetadata().entrySet()) {
                result += meta.getKey();
                result += meta.getValue();
            }
        }

        if(dsc.getDataSource().getApplication()!=null)result += dsc.getDataSource().getApplication().getId();else result+="null";
        if(dsc.getDataSource().getApplication()!=null)result += dsc.getDataSource().getApplication().getType();else result+="null";
        if (dsc.getDataSource().getApplication().getMetadata() != null) {
            for (Map.Entry<String, String> meta : dsc.getDataSource().getApplication().getMetadata().entrySet()) {
                result += meta.getKey();
                result += meta.getValue();
            }
        }
        if(dsc.getDataSource().getPlatform()!=null)result += dsc.getDataSource().getPlatform().getId();else result+="null";
        if(dsc.getDataSource().getPlatform()!=null)result += dsc.getDataSource().getPlatform().getType();else result+="null";

        if (dsc.getDataSource().getPlatform()!=null && dsc.getDataSource().getPlatform().getMetadata() != null) {
            for (Map.Entry<String, String> meta : dsc.getDataSource().getPlatform().getMetadata().entrySet()) {
                result += meta.getKey();
                result += meta.getValue();
            }
        }

        if(dsc.getDataSource().getPlatformApp()!=null)result += dsc.getDataSource().getPlatformApp().getId();else result+="null";
        if(dsc.getDataSource().getPlatformApp()!=null)result += dsc.getDataSource().getPlatformApp().getType();else result+="null";

        if (dsc.getDataSource().getPlatformApp() != null) {
            if (dsc.getDataSource().getPlatformApp().getMetadata() != null) {
                for (Map.Entry<String, String> meta : dsc.getDataSource().getPlatformApp().getMetadata().entrySet()) {
                    result += meta.getKey();
                    result += meta.getValue();
                }
            }
        }


        return result;
    }

}
