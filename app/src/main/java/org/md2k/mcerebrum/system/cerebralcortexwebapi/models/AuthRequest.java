package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthRequest {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;

    public AuthRequest(String userName, String userPassword) {
        this.username = userName;
        this.password = userPassword;
    }

    public Object getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}