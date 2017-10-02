package org.md2k.mcerebrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.configuration.DataFileManager;
import org.md2k.system.provider.DataCPManager;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.app.AppInfoController;
import org.md2k.system.constant.MCEREBRUM;

import java.util.ArrayList;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source status must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public class BootReceiver extends BroadcastReceiver
{
    Context context;
    ApplicationManager applicationManager;
    AppInfoController appInfoController;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        DataManager dataManager;
        dataManager = new DataManager(new DataFileManager(), new DataCPManager(context));
        Log.d("abc","..."+dataManager.getDataCPManager());
        Log.d("abc","..."+dataManager.getDataCPManager().getAppCPs());
        if(dataManager.isStartAtBoot() && dataManager.getDataCPManager().getStudyCP().getStarted()) {
            applicationManager=new ApplicationManager(context, dataManager.getDataCPManager().getAppCPs());
            Log.d("abc","check core");
            if(!applicationManager.isCoreInstalled()) return;
            Log.d("abc","check core - success");
            ArrayList<AppInfoController> a = applicationManager.getByType(MCEREBRUM.APP.TYPE_STUDY);
            Log.d("abc","check study");
            if(a.size()==0) return;
            Log.d("abc","check study - success");
            appInfoController=a.get(0);
/*
            LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                    new IntentFilter("connection"));
*/
//            applicationManager.startMCerebrumService(appInfoController);
//            appInfoController.get
            Log.d("abc","start mcerebrum background");
            Intent intent1=new Intent();
            intent1.setClassName(appInfoController.getAppBasicInfoController().getPackageName(), appInfoController.getAppBasicInfoController().getPackageName()+".ActivityMain");
            intent1.putExtra("background",true);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
/*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("abc","wait..3 sec");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("abc","trying to start");
                    appInfoController.getmCerebrumController().startBackground(null);
                    Log.d("abc","stop mcerebrum...background");
                    applicationManager.stopMCerebrumService(appInfoController);

                }
            }).start();
*/
        }else{
            PugNotification.with(context).load().identifier(1001).title("mCerebrum").smallIcon(R.mipmap.ic_launcher)
                    .message("Data Collection - OFF").autoCancel(true).click(ActivityMain.class).simple().build();

        }
    }
/*
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            appInfoController.getmCerebrumController().startBackground(null);
            stop();
        }
    };
    void stop(){
        applicationManager.stopMCerebrumService(appInfoController);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);

    }
*/
 }
