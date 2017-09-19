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

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.core.access.MCerebrumStatus;
import org.md2k.md2k.system.Info;
import org.md2k.md2k.system.app.AppBasicInfo;
import org.md2k.md2k.system.app.AppInfo;
import org.md2k.md2k.system.app.InstallInfo;
import org.md2k.md2k.system.study.StudyInfo;
import org.md2k.md2k.system.user.UserInfo;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ApplicationManager {
    private AppInfo[] appInfos;
    private AppInfoController[] appInfoControllers;

    //    private AppInfoE[] appInfos;
//    private AppMC[] appMCs;
    public void set(CApp[] cApps) {
        ArrayList<AppInfo> as = new ArrayList<>();
        ArrayList<AppInfoController> acs = new ArrayList<>();
        for (CApp cApp : cApps) {
            if (cApp.getUse_as().equalsIgnoreCase("NOT_IN_USE")) continue;
            AppInfo a = new AppInfo(new AppBasicInfo(), new InstallInfo(), new MCerebrumStatus());
            as.add(a);
            AppInfoController ac = new AppInfoController(a, cApp);
            acs.add(ac);
        }
        appInfos = new AppInfo[as.size()];
        appInfoControllers = new AppInfoController[acs.size()];
        for (int i = 0; i < as.size(); i++) {
            appInfos[i] = as.get(i);
            appInfoControllers[i] = acs.get(i);
        }
        start();
    }

    public void start() {
        for (AppInfoController appInfoController : appInfoControllers) {
            appInfoController.getmCerebrumController().startService();
        }
    }

    public void stop() {
        if(appInfoControllers==null || appInfoControllers.length==0) return;
        for (AppInfoController appInfoController : appInfoControllers) {
            try {
                appInfoController.getmCerebrumController().stopService(null);
            }catch (Exception ignored){

            }
        }
    }

    public ArrayList<AppInfoController> getAppByType(String type) {
        ArrayList<AppInfoController> acs = new ArrayList<>();
        for (AppInfoController appInfoController : appInfoControllers) {
            if (appInfoController.getAppBasicInfoController().isType(type))
                acs.add(appInfoController);
        }
        return acs;
    }

    public void startStudy(StudyInfo studyInfo, UserInfo userInfo) {
        if (!isCoreInstalled()) {
            Toasty.error(MyApplication.getContext(), "Study/DataKit not installed", Toast.LENGTH_SHORT).show();
            Toasty.error(MyApplication.getContext(), "DataKit not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        AppInfoController a = getAppByType(AppInfo.TYPE_STUDY).get(0);
        Bundle bundle = new Bundle();
        Info info = new Info(userInfo, studyInfo, appInfos);
        bundle.putParcelable("info", info);
//        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(a.getAppBasicInfoController().getPackageName());
//        intent.putExtras(bundle);

//        MyApplication.getContext().startActivity(intent);

        a.getmCerebrumController().launch(bundle);
        stop();
    }

    public AppInfoController[] getAppInfoControllers() {
        return appInfoControllers;
    }

    public void setmCerebrumInfo() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if (!appInfoController.getmCerebrumController().isStarted())
                appInfoController.getmCerebrumController().startService();
            else appInfoController.getmCerebrumController().setStatus();
        }
    }

    public boolean isCoreInstalled() {
        return getAppByType(AppInfo.TYPE_STUDY).size() != 0 && getAppByType(AppInfo.TYPE_DATAKIT).size() != 0;
    }

    public boolean isRequiredAppInstalled() {
        for (AppInfoController appInfoController : appInfoControllers)
            if (appInfoController.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)) {
                if (!appInfoController.getInstallInfoController().isInstalled())
                    return false;
            }
        return true;
    }

    public ArrayList<AppInfoController> getRequiredAppNotInstalled() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED) && !appInfo.getInstallInfoController().isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppNotConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)
                    || !appInfo.getInstallInfoController().isInstalled())
                continue;
            if (appInfo.getmCerebrumController().ismCerebrumSupported()
                    && appInfo.getmCerebrumController().isStarted()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && !appInfo.getmCerebrumController().isEqualDefault()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)
                    || !appInfo.getInstallInfoController().isInstalled())
                continue;
            if (appInfo.getmCerebrumController().ismCerebrumSupported()
                    && appInfo.getmCerebrumController().isStarted()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && appInfo.getmCerebrumController().isEqualDefault()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public int[] getInstallStatus() {
        int result[] = new int[3];
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
        if (appInfos == null) return result;
        for (int i = 0; i < appInfoControllers.length; i++) {
            if (!appInfoControllers[i].getInstallInfoController().isInstalled())
                result[2]++;
//            else if (appInfoControllers[i].getInstallInfoController().checkUpdate())
//                result[1]++;
            else result[0]++;
        }
        return result;
    }

    public void reset(String packageName) {
        for (int i = 0; i < appInfoControllers.length; i++)
            if (appInfoControllers[i].getAppBasicInfoController().getPackageName().equals(packageName)) {
                boolean lastResult = appInfoControllers[i].getInstallInfoController().isInstalled();
                appInfoControllers[i].getmCerebrumController().setInitialized(false);
                appInfoControllers[i].getInstallInfoController().setInstalled();
                if (appInfoControllers[i].getInstallInfoController().isInstalled() != lastResult)
                    if (appInfoControllers[i].getInstallInfoController().isInstalled())
                        appInfoControllers[i].getmCerebrumController().startService();
                    else appInfoControllers[i].getmCerebrumController().stopService(null);

            }
    }

    /*
    public AppMC[] getAppMCs() {
        return appMCs;
    }
    public AppInfoE getAppInfo(String packageName){
        for (AppInfoE appInfo : appInfos)
            if (appInfo.getPackageName().equals(packageName))
                return appInfo;
        return null;
    }

    public boolean isRequiredAppInstalled(){
        if(appInfos ==null) return false;
        for(AppInfoE appInfo : appInfos)
            if(appInfo.isRequired() && !appInfo.isInstalled()) return false;
        return true;
    }

    public ArrayList<AppInfoE> getRequiredAppNotInstalled() {
        ArrayList<AppInfoE> appInfos = new ArrayList<>();
        if(this.appInfos ==null) return appInfos;
        for (AppInfoE appInfo : this.appInfos) {
            if (appInfo.isRequired() && !appInfo.isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfoE> getAppConfigured() {
        ArrayList<AppInfoE> appInfos = new ArrayList<>();
        if(appInfos ==null) return appInfos;
        for (AppInfoE appInfo : this.appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && appInfo.getInfo().isConfigured()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfoE> getAppNotConfigured() {
        ArrayList<AppInfoE> a = new ArrayList<>();
        if(appInfos ==null) return a;
        for (AppInfoE appInfo : appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && !appInfo.getInfo().isConfigured()) {
                a.add(appInfo);
            }
        }
        return a;
    }


    public void getInfo() {
        if(appMCs ==null) return;
        for (AppMC appMC : appMCs) {
            appMC.setInfo();
        }
    }
    public AppInfoE getStudy(){
        return get(TYPE_STUDY);
    }
    public AppInfoE getMCerebrum(){
        return get(TYPE_MCEREBRUM);
    }

    public AppInfoE getDataKit(){
        return get(TYPE_DATA_KIT);
    }
    private AppInfoE get(String type){
        if(appInfos ==null) return null;
        for (AppInfoE appInfo : appInfos) {
            if (appInfo.getType() == null) continue;
            if (type.equals(appInfo.getType().toUpperCase()))
                return appInfo;
        }
        return null;

    }

    public AppInfoE[] getAppInfos() {
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
    public AppBasicInfo[] getAppInfo(){
        AppBasicInfo[] a=new AppBasicInfo[appInfos.length];
        for(int i=0;i<appInfos.length;i++)
            a[i]=appInfos[i].getAppInfo();
        return a;
    }
*/

}
