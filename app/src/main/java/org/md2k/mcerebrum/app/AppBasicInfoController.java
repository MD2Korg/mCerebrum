package org.md2k.mcerebrum.app;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.blankj.utilcode.util.AppUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.md2k.system.app.AppBasicInfo;

import java.sql.Timestamp;

public class AppBasicInfoController {
    private AppBasicInfo appBasicInfo;

    public static final String TYPE_STUDY="STUDY";
    public static final String TYPE_MCEREBRUM="MCEREBRUM";
    public static final String TYPE_DATA_KIT="DATAKIT";

    AppBasicInfoController(AppBasicInfo appBasicInfo, CApp capp) {
        this.appBasicInfo = appBasicInfo;
        appBasicInfo.setId(capp.getId());
        appBasicInfo.setType(capp.getType());
        appBasicInfo.setTitle(capp.getTitle());
        appBasicInfo.setSummary(capp.getSummary());
        appBasicInfo.setDescription(capp.getDescription());
        appBasicInfo.setPackageName(capp.getPackage_name());
        appBasicInfo.setIcon(capp.getIcon());
    }

    public Drawable getIcon(Context context) {
        try {
            Drawable icon = AppUtils.getAppIcon(appBasicInfo.getPackageName());
            if (icon != null) return icon;
        } catch (Exception ignored) {
        }
        try {
            if (appBasicInfo.getIcon() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(Constants.CONFIG_MCEREBRUM_DIR() + appBasicInfo.getIcon());
                if (bitmap != null)
                    return new BitmapDrawable(context.getResources(), bitmap);
            }
        } catch (Exception ignored) {

        }
        return new IconicsDrawable(MyApplication.getContext())
                .icon(FontAwesome.Icon.faw_question_circle_o)
                .color(Color.WHITE)
                .sizeDp(128);
            /*AssetManager am = context.getAssets();
            try {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mcerebrum.png")));
            } catch (IOException ignored) {
            }
            return null;
*/
    }

    public boolean isType(String type) {
        return appBasicInfo != null && appBasicInfo.getType().equalsIgnoreCase(type);
    }
    public String getTitle(){
        return appBasicInfo.getTitle();
    }

    public String getSummary() {
        return appBasicInfo.getSummary();
    }

    public String getDescription() {
        return appBasicInfo.getDescription();
    }
    public boolean isUseAs(String u){
        return appBasicInfo.isUseAs(u);
    }

    public String getPackageName() {
        return appBasicInfo.getPackageName();
    }

    public String getType() {
        return appBasicInfo.getType();
    }
}
