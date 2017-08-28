package org.md2k.mcerebrum.data;
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

public class Data {
    private static final String FIRST_TIME_RUNNING="FIRST_TIME_RUNNING";
    private static final String USER_ID="USER_ID";
    private static final String USER_PASSWORD="USER_PASSWORD";
    private static final String USER_TYPE="USER_TYPE";
    private static final String USER_SERVER="USER_SERVER";
    private static final String USER_TOKEN="USER_TOKEN";
    private static final String USER_LOGGEDIN="USER_LOGGEDIN";
    private static final String REFRESH="REFRESH";

    public static final String TYPE_DEFAULT="DEFAULT";
    public static final String TYPE_LOGIN="LOGIN";
    public static final String TYPE_DOWNLOAD="DOWNLOAD";


    public static boolean isFirstTimeRunning(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getBoolean(context, FIRST_TIME_RUNNING, true);
    }
    public static void setFirstTimeRunning(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context, FIRST_TIME_RUNNING, false);
    }
    public static void setUserType(Context context, String value){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,USER_TYPE, value);
    }
    public static String getUserType(Context context) {
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getString(context, USER_TYPE,null);
    }
    public static String getUserId(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getString(context, USER_ID,null);
    }
    public static void setUserId(Context context, String value){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,USER_ID, value);
    }
    public static void setUserPassword(Context context, String value){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,USER_PASSWORD, value);
    }
    public static String getUserPassword(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getString(context, USER_PASSWORD,null);
    }
    public static String getServer(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getString(context, USER_SERVER,null);
    }
    public static void setServer(Context context, String value){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,USER_SERVER, value);
    }
    public static void setToken(Context context, String value){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,USER_TOKEN, value);
    }
    public static String getToken(Context context){
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getString(context, USER_TOKEN,null);
    }


    public static void setRefresh(Context context, boolean value) {
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context, REFRESH, value);
    }
    public static boolean isRefresh(Context context) {
        MySharedPreference mySharedPreference=new MySharedPreference();
        return mySharedPreference.getBoolean(context,REFRESH,false);
    }

}
