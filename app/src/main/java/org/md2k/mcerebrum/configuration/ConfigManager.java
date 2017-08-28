package org.md2k.mcerebrum.configuration;
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

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.commons.storage.StorageRead;
import org.md2k.mcerebrum.commons.storage.StorageReadWrite;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.data.MySharedPreference;
import org.md2k.mcerebrum.data.StudyInfo;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.AssetInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;

import rx.Observable;
import rx.functions.Func1;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class ConfigManager {
    private static final String DOWNLOAD_FROM = ConfigManager.class.getSimpleName() + "_DOWNLOAD_FROM";
    private static final String CONFIG_NAME = ConfigManager.class.getSimpleName() + "_CONFIG_NAME";
    private static final String UPDATED_AT = ConfigManager.class.getSimpleName() + "_UPDATED_AT";
    private static final String DOWNLOAD_URL = ConfigManager.class.getSimpleName() + "_DOWNLOAD_URL";
    private static final String CONFIGURED = ConfigManager.class.getSimpleName() + "_CONFIGURED";


    private static final String DOWNLOAD_FROM_GITHUB = "DOWNLOAD_FROM_GITHUB";
    private static final String DOWNLOAD_FROM_URL = "DOWNLOAD_FROM_URL";
    private static final String DOWNLOAD_FROM_STORAGE = "DOWNLOAD_FROM_STORAGE";
    private static final String GITHUB_DOWNLOAD_LINK = "MD2Korg/mCerebrum-Configuration";

    public Observable<DownloadInfo> downloadAndExtract(final Context context, String text) {
        final String zipFilePath= StorageReadWrite.get(context, StorageType.SDCARD_APPLICATION).getRootDirectory()+"/temp";
        final String zipFileName="config_temp.zip";
        final String unzipFilePath= StorageReadWrite.get(context, StorageType.SDCARD_INTERNAL).getRootDirectory()+"/mCerebrum/";
        final String mCerebrumFilePath="/mCerebrum/org.md2k.mCerebrum/";
        final String mCerebrumFileName="config.zip";
        Observable<DownloadInfo> observable;
        if (text.contains("/"))
            observable = downloadFromUrl(text, zipFilePath, zipFileName);
        else
            observable = downloadFromGitHub(context, text, zipFilePath, zipFileName);
        return observable.flatMap(new Func1<DownloadInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(DownloadInfo downloadInfo) {
                if(!downloadInfo.isCompleted())
                    return Observable.just(downloadInfo);
                else{

                    if(!unzip(zipFilePath+"/"+zipFileName, unzipFilePath))
                        return Observable.error(new Throwable("Failed to unzip"));
                    try {
                        Config config = read(context, StorageType.SDCARD_INTERNAL, mCerebrumFilePath+mCerebrumFileName);
                        StudyInfo.save(context, config);
                        ApplicationManager.save(context, config.getApplications());
                        return Observable.just(downloadInfo);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                }
            }
        });
    }

    private static Observable<AssetInfo> getLatestVersion(final String configName) {
        String[] parts = GITHUB_DOWNLOAD_LINK.split("/");
        Github github = new Github();
        return github.getReleaseLatest(parts[0], parts[1]).map(new Func1<ReleaseInfo, AssetInfo>() {
            @Override
            public AssetInfo call(ReleaseInfo releaseInfo) {
                for (int i = 0; i < releaseInfo.getAssets().length; i++) {
                    if (releaseInfo.getAssets()[i].getName().equals(configName)) {
                        return releaseInfo.getAssets()[i];
                    }
                }
                return null;
            }
        });
    }

    private Observable<Boolean> isUpdateAvailable(final Context context) {
        String configName = getConfigName(context);
        return getLatestVersion(configName).map(new Func1<AssetInfo, Boolean>() {
            @Override
            public Boolean call(AssetInfo assetInfo) {
                if (assetInfo == null) return false;
                String updatedAt = assetInfo.getUpdated_at();
                return !updatedAt.equals(getUpdatedAt(context));
            }
        });
    }

    private static Observable<DownloadInfo> downloadFromUrl(String url, String filePath, String fileName) {
        return new DownloadFile().download(url, filePath, fileName);
    }

    private boolean unzip(String zipFilePath, String unzipFilePath) {
        return unzipFile(zipFilePath, unzipFilePath);
    }

    private static Observable<DownloadInfo> downloadFromGitHub(Context context, String text, final String filePath, final String fileName) {
        return getLatestVersion(text).flatMap(new Func1<AssetInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(AssetInfo assetInfo) {
                return downloadFromUrl(assetInfo.getBrowser_download_url(), filePath, fileName);
            }
        });
    }

    private Config read(Context context, StorageType storageType, String filePath) throws Exception {
        StorageRead storageRead = StorageRead.get(context, storageType);
        return storageRead.readJson(filePath, Config.class);
    }

    private static boolean isFromGitHub(Context context) {
        String value = new MySharedPreference().getString(context, DOWNLOAD_FROM, null);
        return value != null && value.equals(DOWNLOAD_FROM_GITHUB);
    }

    private static String getConfigName(Context context) {
        return new MySharedPreference().getString(context, CONFIG_NAME, null);
    }

    private static String getDownloadUrl(Context context) {
        return new MySharedPreference().getString(context, DOWNLOAD_URL, null);
    }

    private static String getUpdatedAt(Context context) {
        return new MySharedPreference().getString(context, UPDATED_AT, null);
    }
    public boolean isConfigured(Context context){
        return new MySharedPreference().getBoolean(context, CONFIGURED, false);

    }
}
