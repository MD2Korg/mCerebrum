package org.md2k.mcerebrum;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.system.update.Update;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

/*
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * - Nazir Saleheen <nazir.saleheen@gmail.com>
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

public class ServiceDataCollection extends Service {
    ArrayList<String> packageNames;
    String study;
    String dataKit;
    String mCerebrum;
    Subscription subscription;
    Subscription subscriptionCheckUpdate;
    public static final int NOTIFY_ID=98764;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abc","---------------service onCreate()");
        packageNames= AppBasicInfo.get(this);
        dataKit= AppBasicInfo.getDataKit(this);
        study = getPackageName();
        mCerebrum= AppBasicInfo.getMCerebrum(this);
        watchDog();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("abc","---------------service onStartCommand()...start");
        startForeground(NOTIFY_ID, getCompatNotification(this, "Data Collection - ON"));
        return START_STICKY; // or whatever your flag
    }
    void watchDog(){
        subscription=Observable.interval(2,30, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        Log.d("abc","watchdog()...running...");
                        start();
                        checkUpdateIfNecessary();
                        updateNotification();
                        return false;
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("abc","watchdog error().."+e.toString());
                        stop();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }
    void updateNotification(){
        startForeground(NOTIFY_ID, getCompatNotification(this, "Data Collection - ON"));
    }
    void checkUpdateIfNecessary(){
        SharedPreferences sharedPref = getSharedPreferences("update", MODE_PRIVATE);
        long lastChecked= sharedPref.getLong("last_time_checked",0);
        if(System.currentTimeMillis() - lastChecked>=24*60*60*1000L) {
            checkUpdate();
            SharedPreferences.Editor editor = getSharedPreferences("update", MODE_PRIVATE).edit();
            editor.putLong("last_time_checked", System.currentTimeMillis());
            editor.apply();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    void unsubscribe(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        if(subscriptionCheckUpdate!=null && !subscriptionCheckUpdate.isUnsubscribed())
            subscriptionCheckUpdate.unsubscribe();
    }
    @Override
    public void onDestroy(){
        Log.d("abc","------------------------> onDestroy...................");
        unsubscribe();
        stop();
        stopForeground(true);
        super.onDestroy();
    }
    public static Notification getCompatNotification(Context context, String msg) {
        String heading=context.getResources().getString(R.string.app_name);
        Intent myIntent = new Intent(context, ActivityMain.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        if(Update.hasUpdate(context)!=0)
            heading+= " (Update Available)";
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(heading).setContentText(msg).setContentIntent(pendingIntent);
        return builder.build();
    }
    void stop(){
        Log.d("abc","service stop()...");
        for(int i=0;i<packageNames.size();i++){
            if(!AppAccess.getMCerebrumSupported(this, packageNames.get(i))) continue;
            if(packageNames.get(i).equals(study)) continue;
            if(packageNames.get(i).equals(mCerebrum)) continue;
            AppAccess.stopBackground(this, packageNames.get(i));
        }
    }
    void start(){
        for(int i=0;i<packageNames.size();i++){
            if(!AppAccess.getMCerebrumSupported(this, packageNames.get(i))) continue;
            if(packageNames.get(i).equals(study)) continue;
            if(packageNames.get(i).equals(mCerebrum)) continue;
            AppAccess.startBackground(this, packageNames.get(i));
        }
    }
    void checkUpdate(){
        subscriptionCheckUpdate = Update.checkUpdate(this).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }
        });
    }
}
