package org.md2k.mcerebrum.system.cerebralcortexwebapi.interfaces;

import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthRequest;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioBucketsList;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectsListInBucket;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream.DataStream;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CerebralCortexWebApi {

    @GET("/api/v1/auth/")
    Call<AuthResponse> getAccessToken(@Header("Authorization") String accessToken);

    @POST("/api/v1/auth/")
    Call<AuthResponse> authenticateUser(@Body AuthRequest authRequest);



    @GET("/api/v1/object/")
    Call<MinioBucketsList> bucketsList(@Header("Authorization") String authorization);

    @GET("/api/v1/object/{bucket}/")
    Call<MinioObjectsListInBucket> objectsListInBucket(@Header("Authorization") String authorization,
                                                       @Path("bucket") String bucket);

    @GET("/api/v1/object/stats/{bucket}/{resource}")
    Call<MinioObjectStats> getMinioObjectStats(@Header("Authorization") String authorization,
                                               @Path("bucket") String bucket,
                                               @Path("resource") String resource);

    @GET("/api/v1/object/{bucket}/{resource}")
    Call<ResponseBody> downloadMinioObject(@Header("Authorization") String authorization,
                                           @Path("bucket") String bucket,
                                           @Path("resource") String resource);

    @Multipart
    @PUT("/api/v1/stream/zip/")
    Call<ResponseBody> putArchiveDataStreamWithMetadata(
            @Header("Authorization") String authorization,
            @Part("metadata") DataStream jsonMetadata,
            @Part MultipartBody.Part file);

//    @PUT("/api/v1/stream/")
//    Call<ResponseBody> putRawDataStreamWithMetadata(@Header("Authorization") String authorization,
//                                                    @Body DataStream dataStream);

}