package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("user_uuid")
    @Expose
    private String userUuid;
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    /**
     * No args constructor for use in serialization
     *
     */
    public AuthResponse() {
    }

    /**
     *
     * @param accessToken
     * @param userUuid
     */
    public AuthResponse(String userUuid, String accessToken) {
        super();
        this.userUuid = userUuid;
        this.accessToken = accessToken;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}