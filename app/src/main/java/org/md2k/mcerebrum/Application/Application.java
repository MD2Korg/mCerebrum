package org.md2k.mcerebrum.Application;
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
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;

import rx.Observable;
import rx.Subscription;

class Application {
    String id;
    String type;
    String title;
    String summary;
    String description;
    private String packageName;
    String versionName;
    long size;
    String icon;
    String createdAt;
    String publishedAt;
    String downloadLink;
    boolean isUpdateAvailable;

    Observable<DownloadInfo> download(Activity activity){
        DownloadFile downloadFile=new DownloadFile();
        return downloadFile.download(downloadLink, activity.getExternalFilesDir(null)+"/temp",id+".apk");
    }
    void install(Activity activity, int requestCode){
        String fileName=activity.getExternalFilesDir(null)+"/temp"+"/"+id+".apk";
        AppUtils.installApp(activity,fileName, "org.md2k.mcerebrum.provider",requestCode);
    }
    boolean isInstalled(){
        return AppUtils.isInstallApp(packageName);
    }
    void uninstall(Activity activity, int requestCode){
        AppUtils.uninstallApp(activity, packageName, requestCode);
    }
    int getVersionCode(){
        return AppUtils.getAppVersionCode(packageName);
    }
    String getVersionName(){
        return AppUtils.getAppVersionName(packageName);
    }
    boolean isUpdateAvailable(){
        return isInstalled() && !getVersionName().equals(versionName);
    }
    Drawable getIcon(Context context, String filePath){
        if(isInstalled()) return AppUtils.getAppIcon(packageName);
        else{
            String imageInSD = filePath+"/"+icon;
            Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
            return new BitmapDrawable(context.getResources(), bitmap);
        }
    }
}
