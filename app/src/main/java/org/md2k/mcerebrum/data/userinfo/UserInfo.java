package org.md2k.mcerebrum.data.userinfo;
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
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.md2k.mcerebrum.data.MySharedPreference;

import java.io.IOException;

abstract public class UserInfo {
    public static final int TYPE_NOT_DEFINED = -1;
    public static final int TYPE_FREEBIE = 0;
    public static final int TYPE_SERVER = 1;
    public static final int TYPE_DOWNLOAD = 2;
    private static final String USER_TYPE = "USER_TYPE";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_ICON = "USER_ICON";
    private static final String USER_BACKGROUND = "USER_BACKGROUND";

    private static final String NAME_NOT_DEFINED = "<not defined>";

    public static UserInfo getUser(Context context) {
        int userType = new MySharedPreference().getInt(context, USER_TYPE, TYPE_NOT_DEFINED);
        switch (userType) {
            case TYPE_FREEBIE:
                return new UserInfoFreebie();
            case TYPE_SERVER:
                return new UserInfoServer();
            case TYPE_DOWNLOAD:
                return new UserInfoConfigure();
            default:
                return null;
        }
    }

    public static UserInfo createFreebie(Context context) {
        create(context, TYPE_FREEBIE, "Freebie", null, null);
        return new UserInfoFreebie();
    }

    public static UserInfo createServer(Context context, String name, String icon, String background) {
        create(context, TYPE_SERVER, name, icon, background);
        return new UserInfoServer();
    }

    public static UserInfo createDownload(Context context, String name, String icon, String background) {
        create(context, TYPE_DOWNLOAD, name, icon, background);
        return new UserInfoConfigure();
    }

    private static void create(Context context, int type, String name, String icon, String background) {
        setType(context, type);
        setName(context, name);
        setIcon(context, icon);
        setBackground(context, background);
    }

    private static void setType(Context context, int value) {
        new MySharedPreference().set(context, USER_TYPE, value);
    }

    private static void setName(Context context, String value) {
        new MySharedPreference().set(context, USER_NAME, value);
    }

    private static void setIcon(Context context, String value) {
        new MySharedPreference().set(context, USER_ICON, value);
    }

    private static void setBackground(Context context, String value) {
        new MySharedPreference().set(context, USER_BACKGROUND, value);
    }

    public int getType(Context context) {
        return new MySharedPreference().getInt(context, USER_TYPE, TYPE_FREEBIE);
    }

    public String getName(Context context) {
        return new MySharedPreference().getString(context, USER_NAME, NAME_NOT_DEFINED);

    }

    public Drawable getIcon(Context context) {
        String filePath = new MySharedPreference().getString(context, USER_ICON, null);
        try {
            if (filePath != null) {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(filePath));
            }
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mCerebrum.png")));
        } catch (IOException ignored) {
        }
        return null;
    }

    public Drawable getBackground(Context context) {
        String filePath = new MySharedPreference().getString(context, USER_BACKGROUND, null);
        try {
            if (filePath != null) {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(filePath));
            }
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("header.jpg")));
        } catch (IOException ignored) {
        }
        return null;
    }
}
