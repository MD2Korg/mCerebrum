package org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi;

import android.util.Log;

import com.google.gson.Gson;

import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.AuthRequest;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.CCApiErrorMessage;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.MinioBucket;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.MinioBucketsList;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.MinioObjectsListInBucket;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.stream.DataStream;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.utils.ApiUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/******************
 * EXAMPLES

 CerebralCortexWebApi ccService = ApiUtils.getCCService("https://fourtytwo.md2k.org/");
 CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
 AuthResponse ar = ccWebAPICalls.authenticateUser(username, password);


 List<MinioBucket> buckets = ccWebAPICalls.getMinioBuckets(ar.getAccessToken().toString());

 List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(ar.getAccessToken().toString(), buckets.get(0).getBucketName().toString());

 MinioObjectStats object = ccWebAPICalls.getObjectStats(ar.getAccessToken().toString(), buckets.get(0).getBucketName().toString(), "203_mcerebrum_syed_new.pdf");
 MinioObjectStats object = ccWebAPICalls.getObjectStats(ar.getAccessToken().toString(), "configuration", "mperf.zip");


 Boolean result = ccWebAPICalls.downloadMinioObject(ar.getAccessToken().toString(), "configuration", "mperf.zip", "mperf.zip");


 MetadataBuilder metadataBuilder = new MetadataBuilder();
 DataStream dataStreamMetadata = metadataBuilder.buildDataStreamMetadata("datastream", "123", "999", "sampleStream", "zip");
 Boolean resultUpload = ccWebAPICalls.putArchiveDataAndMetadata(ar.getAccessToken().toString(), dataStreamMetadata, "/storage/emulated/0/Android/data/org.md2k.mcerebrum.datakit/files/raw/raw2/2017092217_2.csv.gz");

 */


public class CCWebAPICalls {
    private CerebralCortexWebApi ccService;

    public CCWebAPICalls(CerebralCortexWebApi ccService) {
        this.ccService = ccService;
    }

    public AuthResponse authenticateUser(String userName, String userPassword) {

        AuthRequest authRequest = new AuthRequest(userName, userPassword);
        Call<AuthResponse> call = ccService.authenticateUser(authRequest);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return (AuthResponse) response.body();
            } else {
                Gson gson = new Gson();
                try {
                    CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                    Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                } catch (Exception e) {
                    Log.e("CCWebAPI", "Server URL is not like a Cerebral Cortex instance");

                }
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }


    public List<MinioBucket> getMinioBuckets(String accessToken) {

        Call<MinioBucketsList> call = ccService.bucketsList(accessToken);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return ((MinioBucketsList) response.body()).getMinioBuckets();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    public List<MinioObjectStats> getObjectsInBucket(String accessToken, String bucketName) {


        Call<MinioObjectsListInBucket> call = ccService.objectsListInBucket(accessToken, bucketName);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return ((MinioObjectsListInBucket) response.body()).getBucketObjects();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    public MinioObjectStats getObjectStats(String accessToken, String bucketName, String objectName) {

        Call<MinioObjectStats> call = ccService.getMinioObjectStats(accessToken, bucketName, objectName);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return (MinioObjectStats) response.body();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    public Boolean downloadMinioObject(String accessToken, String bucketName, String objectName, String outputFileName) {


        Call<ResponseBody> call = ccService.downloadMinioObject(accessToken, bucketName, objectName);


        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                return ApiUtils.writeResponseToDisk((ResponseBody) response.body(), outputFileName);
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return false;
        }
    }


    public Boolean putArchiveDataAndMetadata(String accessToken, DataStream metadata, String filePath) {

        MultipartBody.Part fileMultiBodyPart = ApiUtils.getUploadFileMultipart(filePath);

        Call<ResponseBody> call = ccService.putArchiveDataStreamWithMetadata(accessToken, metadata, fileMultiBodyPart);

        try {
            Response response = call.execute();

            if (response.isSuccessful()) {
                Log.d("CCWebAPI", "Successfully uploaded: " + filePath);
                return true;
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(), CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return false;
        }
    }

}
