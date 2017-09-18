package org.md2k.mcerebrum.study;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.configuration.Config;
import org.md2k.mcerebrum.data.MySharedPreference;
import org.md2k.md2k.system.study.StudyInfo;

import java.io.IOException;

public class StudyInfoController{
    private StudyInfo studyInfo;
    private boolean started;
    private static final String STARTED=StudyInfoController.class.getSimpleName()+"_STARTED";

    public StudyInfoController(){
        studyInfo=new StudyInfo();
    }

    public void set(Config config){
        studyInfo.setId(config.getId());
        studyInfo.setType(config.getType().toUpperCase());
        studyInfo.setTitle(config.getTitle());
        studyInfo.setSummary(config.getSummary());
        studyInfo.setDescription(config.getDescription());
        studyInfo.setIcon(config.getIcon());
        studyInfo.setCoverImage(config.getCover_image());
        studyInfo.setVersion(config.getVersion());
        studyInfo.setStartAtBoot(config.isStart_at_boot());
        started= new MySharedPreference().getBoolean(STARTED, false);
    }

    public String getId() {
        return studyInfo.getId();
    }

    public String getType() {
        return studyInfo.getType();
    }

    public String getTitle() {
        return studyInfo.getTitle();
    }

    public String getSummary() {
        return studyInfo.getSummary();
    }

    public String getDescription() {
        return studyInfo.getDescription();
    }

    public String getVersion() {
        return studyInfo.getVersion();
    }

    public boolean isStartAtBoot() {
        return studyInfo.isStartAtBoot();
    }

    public boolean isStarted(){
        return started;
    }
    public void setStarted(boolean value){
        started=value;
        new MySharedPreference().set(STARTED, value);
    }
    public Drawable getIcon(Context context) {
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+studyInfo.getIcon();
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if(bitmap!=null)
                return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mcerebrum.png")));
        } catch (IOException ignored) {
        }
        return null;
    }

    public Drawable getCoverImage(Context context) {
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+studyInfo.getCoverImage();
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if(bitmap!=null)
                return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("header.jpg")));
        } catch (IOException ignored) {
        }
        return null;
    }

    public void clear() {
        setStarted(false);
    }

    public StudyInfo getStudyInfo() {
        return studyInfo;
    }
}
