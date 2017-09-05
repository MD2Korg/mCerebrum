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

import com.blankj.utilcode.util.TimeUtils;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.data.MySharedPreference;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.AssetInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;

import java.io.FileNotFoundException;
import java.io.IOException;

import rx.Observable;
import rx.functions.Func1;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class ConfigManager {
    private Config config;
    private static final String DOWNLOAD_FROM = ConfigManager.class.getSimpleName() + "_DOWNLOAD_FROM";
    private static final String UPDATED_AT = ConfigManager.class.getSimpleName() + "_UPDATED_AT";
    private static final String DOWNLOAD_LINK = ConfigManager.class.getSimpleName() + "_DOWNLOAD_LINK";
    private static final String CONFIGURED = ConfigManager.class.getSimpleName() + "_CONFIGURED";

    private static final String DOWNLOAD_FROM_GITHUB = "DOWNLOAD_FROM_GITHUB";
    private static final String DOWNLOAD_FROM_URL = "DOWNLOAD_FROM_URL";
    private String updatedAt = null;
    private String downloadFrom = null;
    private String downloadLink = null;

    public boolean read() {
        try {
            config = Storage.readJson(Constants.CONFIG_MCEREBRUM_CONFIGFILE(), Config.class);
            if (config == null) return false;
            else {
                save();
                return true;
            }
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public Config getConfig() {
        return config;
    }

    public void loadFromAsset(Context context) {
        try {
            final String zipFileDir = Storage.getRootDirectory(context, StorageType.SDCARD_APPLICATION) + "/mCerebrum/temp";
            final String zipFileName = "config.zip";
            Storage.copyFromAsset(context, zipFileName, zipFileDir);
            unzipFile(zipFileDir + "/" + zipFileName, Constants.CONFIG_ROOT_DIR());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Observable<DownloadInfo> downloadAndExtractDefault(Context context){
        return downloadAndExtract(context, Constants.CONFIG_DEFAULT_FILENAME);
    }

    public Observable<DownloadInfo> downloadAndExtract(final Context context, String url) {
        final String zipFileDir = Storage.getRootDirectory(context, StorageType.SDCARD_APPLICATION) + "/mCerebrum/temp";
        final String zipFileName = "config.zip";

        Observable<DownloadInfo> observable;
        if (url.contains("/")) {
            downloadFrom = DOWNLOAD_FROM_URL;
            downloadLink = url;
            updatedAt = TimeUtils.getNowString();
            observable = new DownloadFile().download(url, zipFileDir, zipFileName);
        } else {
            downloadFrom = DOWNLOAD_FROM_GITHUB;
            downloadLink = url;
            observable = downloadFromGitHub(url, zipFileDir, zipFileName);
        }
        return observable.flatMap(new Func1<DownloadInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(DownloadInfo downloadInfo) {
                if (!downloadInfo.isCompleted())
                    return Observable.just(downloadInfo);
                else {
                    if (!unzipFile(zipFileDir + "/" + zipFileName, Constants.CONFIG_ROOT_DIR()))
                        return Observable.error(new Throwable("Failed to unzip"));
                    else {
                        if (read()) {
                            save();
                            return Observable.just(downloadInfo);
                        } else {
                            return Observable.error(new Throwable("Configuration file not found"));
                        }
                    }
/*
                    try {
                        Config config = read(mCerebrumFilePath);
                        StudyInfo studyInfo=new StudyInfo();
                        studyInfo.save(context, config);
                        ApplicationManager.save(context, config.getApplications());
                        save(context, downloadUrl, configName, downloadFrom, updatedAt);
                        if(studyInfo.getType(context)!=null && studyInfo.getType(context).equals(StudyInfo.FREEBIE)) {
                            UserInfo.setTitle("Freebie");
                            UserInfo.setLoggedIn(false);
                        }
                        else if(studyInfo.getType(context)!=null && studyInfo.getType(context).equals(StudyInfo.CONFIGURED)) {
                            UserInfo.setTitle(studyInfo.getTitle(context));
                            UserInfo.setLoggedIn(false);
                        }
                        return Observable.just(downloadInfo);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
*/
                }
            }
        });
    }

    private Observable<AssetInfo> getLatestVersion(final String configName) {
        String[] parts = Constants.CONFIG_DEFAULT_GITHUB.split("/");
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

    private Observable<Boolean> isUpdateAvailable() {
        String downloadLink = getDownloadLink();
        return getLatestVersion(downloadLink).map(new Func1<AssetInfo, Boolean>() {
            @Override
            public Boolean call(AssetInfo assetInfo) {
                if (assetInfo == null) return false;
                String latestUpdated = assetInfo.getUpdated_at();
                return !latestUpdated.equals(getUpdatedAt());
            }
        });
    }


    private Observable<DownloadInfo> downloadFromGitHub(String text, final String filePath, final String fileName) {
        return getLatestVersion(text).flatMap(new Func1<AssetInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(AssetInfo assetInfo) {
                updatedAt = assetInfo.getUpdated_at();
                return new DownloadFile().download(assetInfo.getBrowser_download_url(), filePath, fileName);
            }
        });
    }

    private String getDownloadLink() {
        return new MySharedPreference().getString(DOWNLOAD_LINK, null);
    }

    private String getUpdatedAt() {
        return new MySharedPreference().getString(UPDATED_AT, null);
    }

    public boolean isConfigured() {
        return new MySharedPreference().getBoolean(CONFIGURED, false);
    }

    private void save() {
        MySharedPreference mySharedPreference = new MySharedPreference();
        mySharedPreference.set(DOWNLOAD_LINK, downloadLink);
        mySharedPreference.set(CONFIGURED, true);
        mySharedPreference.set(DOWNLOAD_FROM, downloadFrom);
        mySharedPreference.set(UPDATED_AT, updatedAt);
    }

    public void clear() {
        MySharedPreference mySharedPreference = new MySharedPreference();
        mySharedPreference.clear(DOWNLOAD_LINK);
        mySharedPreference.clear(CONFIGURED);
        mySharedPreference.clear(DOWNLOAD_FROM);
        mySharedPreference.clear(UPDATED_AT);
        Storage.deleteDir(Constants.CONFIG_MCEREBRUM_ROOT_DIRH());
    }
}
