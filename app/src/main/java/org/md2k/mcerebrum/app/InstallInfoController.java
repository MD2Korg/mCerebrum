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

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.md2k.system.app.InstallInfo;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class InstallInfoController {
    private InstallInfo installInfo;
    private String packageName;
    private static final int REQUEST_CODE = 2000;

    InstallInfoController(InstallInfo installInfo, CApp capp) {
        this.packageName = capp.getPackage_name();
        this.installInfo = installInfo;
        installInfo.setDownloadLink(capp.getDownload_link());
        installInfo.setExpectedVersion(capp.getVersion());
        installInfo.setUpdateOption(capp.getUpdate());
        setInstalled();
    }

    public void setInstalled() {
        installInfo.setInstalled(AppUtils.isInstallApp(packageName));
        if (installInfo.isInstalled()) {
            installInfo.setCurrentVersionName(AppUtils.getAppVersionName(packageName));
            installInfo.setCurrentVersionCode(AppUtils.getAppVersionCode(packageName));
        }
    }
    public boolean hasUpdate(){
        if(installInfo.getUpdateVersionName()==null) return false;
        if(installInfo.getUpdateVersionName().equalsIgnoreCase(installInfo.getCurrentVersionName())) return false;
        return true;
    }
    public Observable<Boolean> checkUpdate() {
        Observable<VersionInfo> observable = null;
        switch (installInfo.getDownloadType()) {
            case GITHUB:
                observable = new AppFromGithub().getVersion(installInfo.getDownloadLink(), installInfo.getExpectedVersion());
                break;
            case JSON:
                observable = new AppFromJson().getVersion(installInfo.getDownloadLink());
                break;
        }
        if (observable != null) {
            return observable.map(new Func1<VersionInfo, Boolean>() {
                @Override
                public Boolean call(VersionInfo versionInfo) {
                    installInfo.setUpdateVersionName(versionInfo.versionName);
                    if(!installInfo.getCurrentVersionName().equalsIgnoreCase(versionInfo.versionName)) {
                        return true;
                    }
                    else return false;
                }
            });
        }
        else return Observable.just(false);
    }

    public Observable<DownloadInfo> install(final Activity activity) {
        final String dirName = Storage.getRootDirectory(activity, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
        final String fileName = "temp.apk";
        Observable<VersionInfo> observable = null;
        switch (installInfo.getDownloadType()) {
            case PLAYSTORE:
                Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(installInfo.getDownloadLink()));
                goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(goToMarket, REQUEST_CODE);
                return Observable.just(new DownloadInfo(0,0,true));
            case GITHUB:
                observable = new AppFromGithub().getVersion(installInfo.getDownloadLink(), installInfo.getExpectedVersion());
                break;
            case JSON:
                observable = new AppFromJson().getVersion(installInfo.getDownloadLink());
                break;
            case URL:
                VersionInfo versionInfo = new VersionInfo();
                versionInfo.downloadURL = installInfo.getDownloadLink();
                observable = Observable.just(versionInfo);
                break;
        }
        if (observable != null) {
            return observable.flatMap(new Func1<VersionInfo, Observable<DownloadInfo>>() {
                @Override
                public Observable<DownloadInfo> call(VersionInfo versionInfo) {
                    if (versionInfo == null) return Observable.error(new Throwable("File can't be downloaded"));
                    return new DownloadFile().download(versionInfo.downloadURL, dirName, fileName).subscribeOn(Schedulers.computation());
                }
            }).map(new Func1<DownloadInfo, DownloadInfo>() {
                @Override
                public DownloadInfo call(DownloadInfo downloadInfo) {
                    if(downloadInfo.isCompleted())
                        AppUtils.installApp(activity, dirName+"/"+fileName, "org.md2k.mcerebrum.provider", REQUEST_CODE);
                    return downloadInfo;
                }
            });
        }
        else return Observable.error(new Throwable("File can't be downloaded"));
    }

/*
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
*/


    public void uninstall(Activity activity, int requestCode) {
        AppUtils.uninstallApp(activity, packageName, requestCode);
    }


    public String getCurrentVersionName() {
        return installInfo.getCurrentVersionName();
    }

    public boolean isInstalled() {
        return installInfo.isInstalled();
    }
}
