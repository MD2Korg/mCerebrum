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
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.configuration.Config;

import java.io.IOException;

public class StudyInfo {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String icon;
    private String coverImage;
    private String version;
    private boolean startAtBoot;
    private static final String STARTED=StudyInfo.class.getSimpleName()+"_STARTED";

    public static final String FREEBIE="FREEBIE";
    public static final String SERVER="SERVER";
    public static final String CONFIGURED="CONFIGURED";

    public void set(Config config){
        id=config.getId();
        type=config.getType().toUpperCase();
        title=config.getTitle();
        summary=config.getSummary();
        description=config.getDescription();
        icon=config.getIcon();
        coverImage=config.getCover_image();
        version=config.getVersion();
        startAtBoot=config.isStart_at_boot();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getVersion() {
        return version;
    }

    public boolean isStartAtBoot() {
        return startAtBoot;
    }

    public boolean isStarted(){
        return new MySharedPreference().getBoolean(STARTED, false);
    }
    public void setStarted(boolean value){
        new MySharedPreference().set(STARTED, value);
    }
    public Drawable getIcon(Context context) {
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+icon;
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
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+coverImage;
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
}
