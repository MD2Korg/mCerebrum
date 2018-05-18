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
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.ServerManager;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.cerebral_cortex.serverinfo.CCInfo;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.config_info.ConfigInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppCP;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.study_info.StudyInfo;
import org.md2k.mcerebrum.system.appinfo.AppInstall;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.blankj.utilcode.util.ZipUtils.unzipFile;


public class ConfigManager {
    public enum LOAD_TYPE{NEW, UPDATE, READ};
    public static boolean load(Context context, LOAD_TYPE l){
        boolean flag=true;
        if(read(context, l)==null) {
            loadFromAsset(context);
            flag=false;
        }
        return flag;
    }
    private static void resetAppCP(Context context, AppFile[] appFiles){
        ArrayList<String> p = AppCP.read(context);
        for(int i=0;i<p.size();i++){
            boolean flag=false;
            for(int j=0;j<appFiles.length;j++)
                if(appFiles[j].getPackage_name().equals(p.get(i))) {
                    flag = true;
                    break;
                }
            if(!flag){
                AppCP.deleteRow(context, p.get(i));
            }
        }
        for (AppFile a : appFiles) {
            AppBasicInfo.set(context, a.getPackage_name(), a.getType(), a.getTitle(), a.getSummary(), a.getDescription(), a.getUse_as(), a.getDownload_link(), a.getUpdate(), a.getExpected_version(), a.getIcon(), true);
        }
    }
    private static void resetAppInstall(Context context, AppFile[] appFiles){
        for (AppFile a : appFiles) {
            AppInstall.set(context, a.getPackage_name());
        }
    }

    private static void resetStudyCP(StudyFile s){
        StudyInfo.deleteAll();
        StudyInfo studyInfo = new StudyInfo(s.getId(), s.getType(), s.getTitle(), s.getSummary(), s.getDescription(), s.getVersion(), s.getIcon(), s.getCover_image(), s.getStart_at_boot(), false);
        StudyInfo.write(studyInfo);
    }
    private static void resetConfigCP(ConfigFile c){
        ConfigInfo.deleteAll();
        ConfigInfo info = new ConfigInfo(c.getId(), c.getType(), c.getTitle(), c.getSummary(), c.getDescription(), c.getVersion(), c.getUpdate(), c.getExpected_version(), null, c.getFile_name(), c.getDownload_link());
        ConfigInfo.write(info);
    }
    private static boolean isNew(Context context, String type, String id){
        String cid = ConfigInfo.getCid();
        String ctype=ConfigInfo.getType();
        if(cid==null || ctype==null) return true;
        if(cid.equalsIgnoreCase(id) && ctype.equalsIgnoreCase(type)) return false;
        return true;
    }
    private static boolean hasUpdate(Context context, String type, String id, String version){
        String cid = ConfigInfo.getCid();
        String ctype=ConfigInfo.getType();
        String cversion = ConfigInfo.getVersion();
        if(cid==null || ctype==null || cversion==null) return true;
        if(cid.equalsIgnoreCase(id) && ctype.equalsIgnoreCase(type) && cversion.equalsIgnoreCase(version)) return false;
        return true;
    }

    public static DataFile read(Context context, LOAD_TYPE l) {
        DataFile dataFile;
        try {
            dataFile = Storage.readJson(Constants.CONFIG_MCEREBRUM_CONFIGFILE(), DataFile.class);
        } catch (Exception ignored) {
            return null;
        }
        if(l==LOAD_TYPE.NEW || l==LOAD_TYPE.UPDATE
                || isNew(context, dataFile.getConfig().getType(), dataFile.getConfig().getId())
                || hasUpdate(context, dataFile.getConfig().getType(), dataFile.getConfig().getId(), dataFile.getConfig().getVersion())){
            resetConfigCP(dataFile.getConfig());
            resetStudyCP(dataFile.getStudy());
            resetAppCP(context, dataFile.getApps());
            resetAppInstall(context, dataFile.getApps());
        }else {
            resetAppCP(context, dataFile.getApps());
            resetAppInstall(context, dataFile.getApps());
        }
        return dataFile;
    }
    public static DataFile loadFromAsset(Context context) {
        DataFile dataFile=null;
        try {
            final String zipFileDir = Storage.getRootDirectory(context, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
            final String zipFileName = "config.zip";
            boolean v = Storage.copyFromAsset(context, zipFileName, zipFileDir+"/"+zipFileName);
            unzipFile(zipFileDir + "/" + zipFileName, Constants.CONFIG_ROOT_DIR());
            dataFile = Storage.readJson(Constants.CONFIG_MCEREBRUM_CONFIGFILE(), DataFile.class);
        } catch (IOException ignored) {
            Log.d("abc","error ->"+ignored.getMessage());
        }
        CCInfo.deleteAll();
        resetConfigCP(dataFile.getConfig());
        ConfigInfo.setDownloadFrom(MCEREBRUM.CONFIG.TYPE_FREEBIE);

        resetStudyCP(dataFile.getStudy());
        resetAppCP(context, dataFile.getApps());
        resetAppInstall(context, dataFile.getApps());
        return dataFile;
    }
    public static Observable<Boolean> updateConfigServer(final Context context, final String dir) {
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {

                if (!ServerManager.download(CCInfo.getUrl(), CCInfo.getUserName(), CCInfo.getPasswordHash(), CCInfo.getConfigFileName()))
                    return Observable.error(new Throwable("Download failed"));
                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        String a = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        if (unzipFile(a + "/config.zip", dir)==null)
                            return Observable.error(new Throwable("Failed to unzip"));
                        else {
                            if(!ConfigManager.load(context, LOAD_TYPE.UPDATE)){
                                return Observable.error(new Throwable("Configuration file format error"));
                            }else {
                                MinioObjectStats minioObject=ServerManager.getConfigFile(CCInfo.getUrl(), CCInfo.getUserName(), CCInfo.getPasswordHash(), CCInfo.getConfigFileName());
                                CCInfo.setCurrentVersion(minioObject.getLastModified());
                                CCInfo.setLatestVersion(minioObject.getLastModified());
                                ConfigInfo.setDownloadFrom(MCEREBRUM.CONFIG.TYPE_SERVER);
                                return Observable.just(true);
                            }
                        }
                    }

                });
    }
}
