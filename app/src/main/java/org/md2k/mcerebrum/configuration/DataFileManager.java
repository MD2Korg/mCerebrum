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
import android.os.Environment;
import android.util.Log;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.system.cerebralcortexwebapi.CCWebAPICalls;
import org.md2k.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.system.cerebralcortexwebapi.utils.ApiUtils;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.internet.download.DownloadFile;
import org.md2k.system.internet.download.DownloadInfo;
import org.md2k.system.internet.github.model.AssetInfo;
import org.md2k.system.internet.github.model.ReleaseInfo;
import org.md2k.system.internet.github.service.Github;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;

public class DataFileManager {
    private DataFile dataFile;

    public DataFileManager() {
        read();
        if (dataFile == null)
            loadFromAsset();
    }

    public boolean read() {
        try {
            dataFile = null;
            dataFile = Storage.readJson(Constants.CONFIG_MCEREBRUM_CONFIGFILE(), DataFile.class);
            if(dataFile==null) return false;
            return true;
        } catch (Exception ignored) {
            return false;
        }

    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public void loadFromAsset() {
        try {
            dataFile=null;
            Context context = MyApplication.getContext();
            final String zipFileDir = Storage.getRootDirectory(context, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
            final String zipFileName = "config.zip";
            boolean v = Storage.copyFromAsset(context, zipFileName, zipFileDir+"/"+zipFileName);
            unzipFile(zipFileDir + "/" + zipFileName, Constants.CONFIG_ROOT_DIR());
            dataFile = Storage.readJson(Constants.CONFIG_MCEREBRUM_CONFIGFILE(), DataFile.class);
        } catch (IOException ignored) {
            Log.d("abc","error ->"+ignored.getMessage());
        }
    }

    public Observable<DownloadInfo> downloadAndExtractDefault(Context context) {
        return downloadAndExtract(context, Constants.CONFIG_DEFAULT_FILENAME);
    }

    private Observable<DownloadInfo> downloadURL(String url, String dir, String file) {
        return new DownloadFile().download(url, dir, file);
    }

    private Observable<DownloadInfo> downloadGitHub(String url, final String dir, final String file) {
        return getLatestVersion(url).flatMap(new Func1<AssetInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(AssetInfo assetInfo) {
                return new DownloadFile().download(assetInfo.getBrowser_download_url(), dir, file);
            }
        });
    }
/*
    public boolean checkUpdateServer(String serverName, String userName, String password, long lastUpdate) {
        CerebralCortexWebApi ccService;
        CCWebAPICalls ccWebAPICalls;
        ccService = ApiUtils.getCCService(serverName);
        ccWebAPICalls = new CCWebAPICalls(ccService);

        AuthResponse ar = ccWebAPICalls.authenticateUser(userName, password);
        if (ar == null) return false;
        List<MinioBucket> buckets = ccWebAPICalls.getMinioBuckets(ar.getAccessToken());
        if (buckets == null || buckets.size() == 0) return false;
        List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(ar.getAccessToken(), buckets.get(0).getBucketName().toString());
        if(objectList==null || objectList.size()==0) return false;
        if(Double.parseDouble(objectList.get(0).getLastModified())<=lastUpdate) return false;
        return true;
    }
*/
/*
    public Observable<DownloadInfo> downloadAndExtractFromServer(String serverName, String userName, String password) {
        CerebralCortexWebApi ccService;
        final CCWebAPICalls ccWebAPICalls;
        ccService = ApiUtils.getCCService(serverName);
        ccWebAPICalls = new CCWebAPICalls(ccService);

        final AuthResponse ar = ccWebAPICalls.authenticateUser(userName, password);
        if (ar == null) return Observable.error(new Throwable("Authentication failed"));
//        final List<MinioBucket> buckets = ccWebAPICalls.getMinioBuckets(ar.getAccessToken());
//        if (buckets == null || buckets.size() == 0) return Observable.error(new Throwable("File not found"));
        final List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(ar.getAccessToken(), "configuration");
        if (objectList == null || objectList.size() == 0)
            return Observable.error(new Throwable("File not found"));

    }
*/
    int ind=0;

    public Observable<DownloadInfo> downloadAndExtractFromServer(String serverName, String userName, String password){
        CerebralCortexWebApi ccService;
        final CCWebAPICalls ccWebAPICalls;
        ccService = ApiUtils.getCCService(serverName);
        ccWebAPICalls = new CCWebAPICalls(ccService);

        final AuthResponse ar = ccWebAPICalls.authenticateUser(userName, password);
        if (ar == null) return Observable.error(new Throwable("Authentication failed"));
//        final List<MinioBucket> buckets = ccWebAPICalls.getMinioBuckets(ar.getAccessToken());
//        if (buckets == null || buckets.size() == 0) return Observable.error(new Throwable("File not found"));
        final List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(ar.getAccessToken(), "configuration");
        if(objectList==null || objectList.size()==0) return Observable.error(new Throwable("File not found"));
/*
        for(int i=0;i<objectList.size();i++)
            if(objectList.get(i).getObjectName().equals(filename)){
                ind=i;
                break;
            }
*/
                return Observable.just(objectList.get(ind))
                .flatMap(new Func1<MinioObjectStats, Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(MinioObjectStats minioObjectStats) {
                        Boolean result = ccWebAPICalls.downloadMinioObject(ar.getAccessToken(), "configuration", objectList.get(ind).getObjectName(), "config.zip");
                        if(!result) return Observable.error(new Throwable("Download failed"));
                        return Observable.just(new DownloadInfo(0,0, true));
                    }
                }).flatMap(new Func1<DownloadInfo, Observable<DownloadInfo>>() {
                    @Override
                    public Observable<DownloadInfo> call(DownloadInfo downloadInfo) {
                        String a = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        if (!unzipFile(a+"/config.zip", Constants.CONFIG_ROOT_DIR()))
                            return Observable.error(new Throwable("Failed to unzip"));
                        else {
                            read();
                            if (dataFile != null)
                                return Observable.just(downloadInfo);
                            else {
                                return Observable.error(new Throwable("Configuration file not found"));
                            }
                        }
                    }
                });

    }


    public Observable<DownloadInfo> downloadAndExtract(final Context context, String url) {
        final String zipFileDir = Storage.getRootDirectory(MyApplication.getContext(), StorageType.SDCARD_APPLICATION) + "/mCerebrum/temp";
        final String zipFileName = "config.zip";
        Observable<DownloadInfo> observable;
        switch (MCEREBRUM.CONFIG.getDownloadType(url)) {
            case GITHUB:
                observable = downloadGitHub(url, zipFileDir, zipFileName);
                break;
            case URL:
                observable = downloadURL(url, zipFileDir, zipFileName);
                break;
            default:
                observable = Observable.error(new Throwable("abc"));

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
                        read();
                        if (dataFile != null)
                            return Observable.just(downloadInfo);
                        else {
                            return Observable.error(new Throwable("Configuration file not found"));
                        }
                    }
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

/*
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
*/

}
