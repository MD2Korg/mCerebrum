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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

public class AppInstall {
    private AppInfo appInfo;
    private static final int REQUEST_CODE = 2000;

    public AppInstall(AppInfo appInfo){
        this.appInfo=appInfo;
    }
    public void install(Activity activity){
        if(appInfo.getDownloadFromPlayStore()!=null){
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(appInfo.getDownloadFromPlayStore()));
            goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(goToMarket, REQUEST_CODE);
        }else{
            final String fileDir = Storage.getRootDirectory(activity, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
            final String fileName = "temp.apk";

            AppUtils.installApp(activity, fileDir+"/"+fileName, "org.md2k.mcerebrum.provider", REQUEST_CODE);
        }
    }
    public Observable<DownloadInfo> download(final Activity activity) {
        final String fileDir = Storage.getRootDirectory(activity, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
        final String fileName = "temp.apk";

        Observable<DownloadInfo> observable;
        if (appInfo.getDownloadFromURL() != null)
            observable = new DownloadFile().download(appInfo.getDownloadFromURL(), fileDir, fileName);
        else {
            if (appInfo.getExpectedVersion() == null)
                observable = downloadLatest(fileDir, fileName);
            else {
                observable = getVersions().flatMap(new Func1<ReleaseInfo[], Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(ReleaseInfo[] releaseInfos) {
                        for (ReleaseInfo releaseInfo : releaseInfos) {
                            if (releaseInfo.getName().equals(appInfo.getExpectedVersion())) {
                                for (int j = 0; j < releaseInfo.getAssets().length; j++)
                                    if (releaseInfo.getAssets()[j].getName().endsWith(".apk"))
                                        return new DownloadFile().download(releaseInfo.getAssets()[j].getBrowser_download_url(), fileDir, fileName);
                            }
                        }
                        return Observable.error(new Throwable("File not found in the server"));
                    }
                });
            }
        }
        return observable;
    }

    private Observable<DownloadInfo> downloadLatest(final String filePath, final String fileName) {
        return getLatestVersion().map(new Func1<ReleaseInfo, String>() {
            @Override
            public String call(ReleaseInfo releaseInfo) {
                for (int i = 0; i < releaseInfo.getAssets().length; i++) {
                    if (releaseInfo.getAssets()[i].getBrowser_download_url().endsWith(".apk"))
                        return releaseInfo.getAssets()[i].getBrowser_download_url();
                }
                return null;
            }
        }).flatMap(new Func1<String, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(String s) {
                if (s == null) return null;
                return new DownloadFile().download(s, filePath, fileName);
            }
        });
    }

    private Observable<ReleaseInfo[]> getVersions() {
        String[] parts = appInfo.getDownloadFromGithub().split("/");
        if (parts.length != 2) return Observable.error(new Throwable("Invalid download link in configuration file"));
        Github github = new Github();
        return github.getReleases(parts[0], parts[1]);
    }

    private Observable<ReleaseInfo> getLatestVersion() {
        String[] parts = appInfo.getDownloadFromGithub().split("/");
        if (parts.length != 2) return null;
        Github github = new Github();
        return github.getReleaseLatest(parts[0], parts[1]);
    }

    public void uninstall(Activity activity, int requestCode) {
        AppUtils.uninstallApp(activity, appInfo.getPackageName(), requestCode);
    }

}
