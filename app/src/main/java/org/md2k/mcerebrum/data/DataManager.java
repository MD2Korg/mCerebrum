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

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.configuration.AppFile;
import org.md2k.mcerebrum.configuration.ConfigFile;
import org.md2k.mcerebrum.configuration.DataFileManager;
import org.md2k.mcerebrum.configuration.StudyFile;
import org.md2k.system.app.AppFromJson;
import org.md2k.system.app.VersionInfo;
import org.md2k.system.provider.DataCPManager;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.provider.StudyCP;
import org.md2k.system.provider.UserCP;

import rx.Observable;
import rx.functions.Func1;

public class DataManager {
    private DataFileManager dataFileManager;
    private DataCPManager dataCPManager;
    public DataManager(DataFileManager dataFileManager, DataCPManager dataCPManager){
        this.dataCPManager = dataCPManager;
        this.dataFileManager = dataFileManager;
        resetDataCP();
    }
    public void resetDataCP(){
        try {
            if (dataFileManager.getDataFile() == null || dataFileManager.getDataFile().getConfig()==null)
                dataFileManager.loadFromAsset();
            if (dataCPManager.isNew(dataFileManager.getDataFile().getConfig().getId(), dataFileManager.getDataFile().getConfig().getType())) {
                dataCPManager.deleteForNew();
                prepareDP();
            }
        }catch (Exception e){

        }
    }
    private void prepareDP(){
        ConfigFile configFile = dataFileManager.getDataFile().getConfig();
        dataCPManager.setConfigCP(configFile.getId(), configFile.getType(), configFile.getTitle(), configFile.getSummary(), configFile.getDescription(), configFile.getVersion(), configFile.getUpdate(), configFile.getExpected_version(), null, configFile.getDownload_link(), DateTime.getDateTime()/1000);
        StudyFile studyFile = dataFileManager.getDataFile().getStudy();
        dataCPManager.setStudyCP(studyFile.getId(), studyFile.getType(), studyFile.getTitle(), studyFile.getSummary(), studyFile.getDescription(), studyFile.getVersion(), studyFile.getIcon(), studyFile.getCover_image(), studyFile.getStart_at_boot(), false);
        AppFile[] appFiles = dataFileManager.getDataFile().getApps();
        for (AppFile appFile : appFiles) {
            if(appFile.getUse_as().equalsIgnoreCase(MCEREBRUM.APP.USE_AS_NOT_IN_USE)) continue;
            dataCPManager.setAppCPs(appFile.getId(), appFile.getType(), appFile.getTitle(), appFile.getSummary(), appFile.getDescription(), appFile.getPackage_name(), appFile.getDownload_link(), appFile.getUpdate(), appFile.getUse_as(), appFile.getExpected_version(), appFile.getIcon(), null, null, false, false, false);
        }
    }
    public void updateDataDP(){
        dataCPManager.deleteForUpdate();
        prepareDP();
    }

    public DataFileManager getDataFileManager() {
        return dataFileManager;
    }

    public DataCPManager getDataCPManager() {
        return dataCPManager;
    }
    public boolean isStartAtBoot(){
        if(dataCPManager.getStudyCP().getStartAtBoot() && dataCPManager.getStudyCP().getStarted())
            return true;
        return false;
    }

    public Observable<Boolean> checkUpdateConfig() {
        dataCPManager.getConfigCP().setLatestVersion(MyApplication.getContext(), dataCPManager.getConfigCP().getConfigInfoBean().getVersions());
        if(MCEREBRUM.CONFIG.UPDATE_TYPE_NEVER.equalsIgnoreCase(dataCPManager.getConfigCP().getConfigInfoBean().getUpdates()))
            return Observable.just(false);
        if(dataCPManager.getConfigCP().getConfigInfoBean().getExpectedVersion()!=null) return Observable.just(false);
        if(dataCPManager.getConfigCP().getConfigInfoBean().getDownloadLink()==null) return Observable.just(false);

        if (dataCPManager.getConfigCP().getType().equalsIgnoreCase(MCEREBRUM.CONFIG.TYPE_SERVER)) {
            return Observable.just(true).map(new Func1<Boolean, Boolean>() {
                @Override
                public Boolean call(Boolean aBoolean) {
                    UserCP userCP=dataCPManager.getUserCP();
                    boolean isUpdate = dataFileManager.checkUpdateServer(dataCPManager.getConfigCP().getConfigInfoBean().getDownloadLink(), userCP.getTitle(), userCP.getPasswordHash(), dataCPManager.getConfigCP().getConfigInfoBean().getLastUpdated());
                    if(isUpdate){
                        dataCPManager.getConfigCP().setLatestVersion(MyApplication.getContext(), "YES");
                    }else{
                        dataCPManager.getConfigCP().setLatestVersion(MyApplication.getContext(), dataCPManager.getConfigCP().getConfigInfoBean().getVersions());
                    }
                    return isUpdate;
                }
            });
        }else{
            return new AppFromJson().getVersion(MyApplication.getContext(), dataCPManager.getConfigCP().getConfigInfoBean().getDownloadLink())
                    .map(new Func1<VersionInfo, Boolean>() {
                        @Override
                        public Boolean call(VersionInfo versionInfo) {
                            if(versionInfo.versionName.equalsIgnoreCase(dataCPManager.getConfigCP().getConfigInfoBean().getVersions())) {
                                dataCPManager.getConfigCP().setLatestVersion(MyApplication.getContext(), dataCPManager.getConfigCP().getConfigInfoBean().getVersions());
                                return false;
                            }
                            else {
                                dataCPManager.getConfigCP().setLatestVersion(MyApplication.getContext(), versionInfo.versionName);
                                return true;
                            }
                        }
                    });

        }
    }
}
