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

import org.md2k.mcerebrum.configuration.CApp;

import java.util.ArrayList;

public class ApplicationManager {
    private AppInfo[] appInfos;
    private AppMC[] appMCs;
    public static final String TYPE_STUDY="STUDY";
    public static final String TYPE_MCEREBRUM="MCEREBRUM";
    public static final String TYPE_DATA_KIT="DATAKIT";

    public void set(CApp[] cApps){
        ArrayList<AppInfo> appInfos=new ArrayList<>();
        for (CApp cApp : cApps) {
            AppInfo a=new AppInfo(cApp);
            if (a.isNotInUse()) continue;
            appInfos.add(a);
        }
        this.appInfos =new AppInfo[appInfos.size()];
        this.appMCs =new AppMC[appInfos.size()];
        for(int i=0;i<appInfos.size();i++){
            this.appInfos[i]=appInfos.get(i);
            this.appMCs[i]=new AppMC(appInfos.get(i));
        }
    }

    public AppMC[] getAppMCs() {
        return appMCs;
    }
    public AppInfo getAppInfo(String packageName){
        for (AppInfo appInfo : appInfos)
            if (appInfo.getPackageName().equals(packageName))
                return appInfo;
        return null;
    }

    public int[] getInstallStatus(){
        int result[]=new int[3];
        result[0]=0;result[1]=0;result[2]=0;
        if(appInfos ==null) return result;
        for (AppInfo appInfo : appInfos) {
            if (!appInfo.isInstalled())
                result[2]++;
            else if (appInfo.isUpdateAvailable())
                result[1]++;
            else result[0]++;
        }
        return result;
    }
    public boolean isRequiredAppInstalled(){
        if(appInfos ==null) return false;
        for(AppInfo appInfo : appInfos)
            if(appInfo.isRequired() && !appInfo.isInstalled()) return false;
        return true;
    }

    public ArrayList<AppInfo> getRequiredAppNotInstalled() {
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        if(this.appInfos ==null) return appInfos;
        for (AppInfo appInfo : this.appInfos) {
            if (appInfo.isRequired() && !appInfo.isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfo> getAppConfigured() {
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        if(appInfos ==null) return appInfos;
        for (AppInfo appInfo : this.appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && appInfo.getInfo().isConfigured()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfo> getAppNotConfigured() {
        ArrayList<AppInfo> a = new ArrayList<>();
        if(appInfos ==null) return a;
        for (AppInfo appInfo : appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && !appInfo.getInfo().isConfigured()) {
                a.add(appInfo);
            }
        }
        return a;
    }

    public void stop() {
        if(appMCs ==null) return;
        for (AppMC appMC : appMCs) appMC.stopService();
    }

    public void getInfo() {
        if(appMCs ==null) return;
        for (AppMC appMC : appMCs) {
            appMC.setInfo();
        }
    }
    public AppInfo getStudy(){
        return get(TYPE_STUDY);
    }
    public AppInfo getMCerebrum(){
        return get(TYPE_MCEREBRUM);
    }

    public AppInfo getDataKit(){
        return get(TYPE_DATA_KIT);
    }
    private AppInfo get(String type){
        if(appInfos ==null) return null;
        for (AppInfo appInfo : appInfos) {
            if (appInfo.getType() == null) continue;
            if (type.equals(appInfo.getType().toUpperCase()))
                return appInfo;
        }
        return null;

    }

    public AppInfo[] getAppInfos() {
        return appInfos;
    }

    public void reset(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName)){
                boolean lastResult=appInfos[i].isInstalled();
                appInfos[i].setInitialized(false);
                appInfos[i].setInstalled();
                if(appInfos[i].isInstalled()!=lastResult)
                    if(appInfos[i].isInstalled())
                        appMCs[i].startService();
                else appMCs[i].stopService();
            }
    }

    public void setInfo() {
        for (int i=0;i<appInfos.length;i++) {
            if (!appInfos[i].isInstalled()) continue;
            if (!appInfos[i].isMCerebrumSupported()) continue;
            appMCs[i].setInfo();
        }
    }

    public void configure(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName))
                appMCs[i].configure();
    }
    public void clear(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName))
                appMCs[i].clear();
    }

}
