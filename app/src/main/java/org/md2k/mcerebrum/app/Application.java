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
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.core.access.Info;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;

import java.io.IOException;

import rx.Observable;
import rx.functions.Func1;

public class Application {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String packageName;
    private String icon;
    private String downloadFromGithub;
    private String downloadFromPlayStore;
    private String downloadFromURL;
    private String expectedVersion;
    private String updateOption;
    private String updateVersionName;
    private String currentVersionName;
    private int currentVersionCode;
    private boolean installed;
    private boolean isConfigurable;

    private static final int REQUEST_CODE = 2000;
    private boolean configured;
    private boolean runInBackground;
    private long runningTime;

    Application(CApp capp) {
        id = capp.getId();
        type = capp.getType();
        title = capp.getTitle();
        summary = capp.getSummary();
        description = capp.getDescription();
        packageName = capp.getPackage_name();
        icon = capp.getIcon();
        downloadFromGithub = capp.getDownload_from_github();
        downloadFromPlayStore = capp.getDownload_from_playstore();
        downloadFromURL = capp.getDownload_from_url();
        expectedVersion = capp.getVersion();
        updateOption = capp.getUpdate();
        installed = AppUtils.isInstallApp(packageName);
        if (installed) {
            currentVersionName = AppUtils.getAppVersionName(packageName);
            currentVersionCode = AppUtils.getAppVersionCode(packageName);
        }
    }

    public void install(Activity activity){
        if(downloadFromPlayStore!=null){
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(downloadFromPlayStore));
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
        if (downloadFromURL != null)
            observable = new DownloadFile().download(downloadFromURL, fileDir, fileName);
        else {
            if (getExpectedVersion() == null)
                observable = downloadLatest(fileDir, fileName);
            else {
                observable = getVersions().flatMap(new Func1<ReleaseInfo[], Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(ReleaseInfo[] releaseInfos) {
                        for (ReleaseInfo releaseInfo : releaseInfos) {
                            if (releaseInfo.getName().equals(getExpectedVersion())) {
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
        String[] parts = downloadFromGithub.split("/");
        if (parts.length != 2) return Observable.error(new Throwable("Invalid download link in configuration file"));
        Github github = new Github();
        return github.getReleases(parts[0], parts[1]);
    }

    private Observable<ReleaseInfo> getLatestVersion() {
        String[] parts = downloadFromGithub.split("/");
        if (parts.length != 2) return null;
        Github github = new Github();
        return github.getReleaseLatest(parts[0], parts[1]);
    }

    public void uninstall(Activity activity, int requestCode) {
        AppUtils.uninstallApp(activity, packageName, requestCode);
    }

    int getCurrentVersionCode() {
        return currentVersionCode;
    }

    public String getCurrentVersionName() {
        return currentVersionName;
    }

    boolean isUpdateAvailable() {
        if (updateVersionName == null) return false;
        if (!isInstalled()) return false;
        if (getCurrentVersionName().equals(updateVersionName)) return false;
        return true;
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

    public Drawable getIcon(Context context) {
        if (isInstalled()) return AppUtils.getAppIcon(packageName);
        else {
            try {
                if (icon != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(Constants.CONFIG_MCEREBRUM_DIR()+icon);
                    if (bitmap != null)
                        return new BitmapDrawable(context.getResources(), bitmap);
                }
            } catch (Exception ignored) {

            }
            AssetManager am = context.getAssets();
            try {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mcerebrum.png")));
            } catch (IOException ignored) {
            }
            return null;

        }
    }


    public String getDownloadFromGithub() {
        return downloadFromGithub;
    }

    public String getDownloadFromPlayStore() {
        return downloadFromPlayStore;
    }

    public String getDownloadFromURL() {
        return downloadFromURL;
    }

    private String getExpectedVersion() {
        return expectedVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void updateInfo() {
        installed = AppUtils.isInstallApp(packageName);
        if (installed) {
            currentVersionName = AppUtils.getAppVersionName(packageName);
            currentVersionCode = AppUtils.getAppVersionCode(packageName);
        }
    }

    public void updateStatus(Info info) {
        isConfigurable = info.isConfigurable();
        runningTime = info.getRunningTime();
        runInBackground = info.isRunInBackground();
        configured = info.isConfigured();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public String getUpdateOption() {
        return updateOption;
    }

    public String getUpdateVersionName() {
        return updateVersionName;
    }

    public boolean isConfigurable() {
        return isConfigurable;
    }

    public static int getRequestCode() {
        return REQUEST_CODE;
    }

    public boolean isConfigured() {
        return configured;
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public long getRunningTime() {
        return runningTime;
    }
}
