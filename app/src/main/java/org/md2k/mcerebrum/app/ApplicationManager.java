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

import android.content.Context;

import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.data.MySharedPreference;

import java.util.ArrayList;

public class ApplicationManager {
    private Application[] applications;
    public static final String TYPE_STUDY="STUDY";
    public static final String TYPE_MCEREBRUM="MCEREBRUM";
    public static final String TYPE_DATA_KIT="DATAKIT";

    public void set(CApp[] cApps){
        clear();
        ArrayList<Application> applicationArrayList=new ArrayList<>();
        for (CApp cApp : cApps) {
            Application application = new Application(cApp);
            if (application.isNotInUse()) continue;
            applicationArrayList.add(application);
        }
        applications=new Application[applicationArrayList.size()];
        for(int i=0;i<applicationArrayList.size();i++){
            applications[i]=applicationArrayList.get(i);
        }
    }

    public Application[] getApplications() {
        return applications;
    }
    public Application getApplication(String packageName){
        for (Application application : applications)
            if (application.getPackageName().equals(packageName))
                return application;
        return null;
    }

    public int[] getInstallStatus(){
        int result[]=new int[3];
        result[0]=0;result[1]=0;result[2]=0;
        if(applications==null) return result;
        for (Application application : applications) {
            if (!application.isInstalled())
                result[2]++;
            else if (application.isUpdateAvailable())
                result[1]++;
            else result[0]++;
        }
        return result;
    }
    public boolean isInstalledRequired(){
        if(applications==null) return false;
        for(Application application: applications)
            if(application.isRequired() && !application.isInstalled()) return false;
        return true;
    }


    public void clear() {
        if(applications==null) return;
        for(int i=0;i<applications.length;i++)
            applications[i].stopService();
    }

    public void updateInfo() {
        if(applications==null) return;
        for (Application application : applications) {
            application.updateInfo();
        }
    }
    public Application getStudy(){
        return get(TYPE_STUDY);
    }
    public Application getMCerebrum(){
        return get(TYPE_MCEREBRUM);
    }

    public Application getDataKit(){
        return get(TYPE_DATA_KIT);
    }
    public Application get(String type){
        if(applications==null) return null;
        for (Application application : applications) {
            if (application.getType() == null) continue;
            if (type.equals(application.getType().toUpperCase()))
                return application;
        }
        return null;

    }
}
