package org.md2k.mcerebrum.login;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.mcerebrum.data.Data;

public class UserInfo implements Parcelable{
    public static final String TYPE_DEFAULT="DEFAULT";
    public static final String TYPE_LOGIN="LOGIN";
    public static final String TYPE_DOWNLOAD="DOWNLOAD";

    private String userType;
    private String userId;
    private String password;
    private String server;
    private String token;

    public UserInfo(Context context){
        Data data=new Data();
        userType=data.getUserType(context);
        userId=data.getUserId(context);
        password=data.getUserPassword(context);
        server=data.getServer(context);
        token=data.getToken(context);
    }
    public UserInfo(String userType, String userId, String password, String server, String token) {
        this.userType=userType;
        this.userId = userId;
        this.password = password;
        this.server = server;
        this.token = token;
    }

    protected UserInfo(Parcel in) {
        userType=in.readString();
        userId = in.readString();
        password = in.readString();
        server = in.readString();
        token = in.readString();
    }
    private void save(Context context){
        Data data=new Data();
        data.setUserType(context, userType);
        data.setUserId(context, userId);
        data.setUserPassword(context, password);
        data.setServer(context, server);
        data.setToken(context, token);
    }

    public String getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public String getToken() {
        return token;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userType);
        dest.writeString(userId);
        dest.writeString(password);
        dest.writeString(server);
        dest.writeString(token);
    }
}
