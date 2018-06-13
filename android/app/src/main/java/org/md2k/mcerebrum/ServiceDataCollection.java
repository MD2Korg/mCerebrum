package org.md2k.mcerebrum;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ServiceUtils;

import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.phonesensor.ServicePhoneSensor;
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
    String dataKit;
    String mCerebrum;
    Subscription subscription;
    public static final int NOTIFY_ID = 98764;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abc", "---------------service onCreate()");
        packageNames = AppBasicInfo.get(this);
        dataKit = AppBasicInfo.getDataKit(this);
        mCerebrum = AppBasicInfo.getMCerebrum(this);
        watchDog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("abc", "---------------service onStartCommand()...start");
        startForeground(NOTIFY_ID, getCompatNotification(this, "Data Collection - ON"));
        return START_STICKY; // or whatever your flag
    }

    void watchDog() {
        final boolean[] count = {false};
        subscription = Observable.interval(2, 30, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        Log.d("abc", "watchdog()...running...");
                        start();
                        if(count[0] ==false)
                        updateNotification();
                        count[0]=true;
                        return false;
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("abc", "watchdog error().." + e.toString());
                        stop();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    void updateNotification() {
        startForeground(NOTIFY_ID, getCompatNotification(this, "Data Collection - ON"));
    }

    /*
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
    */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        Log.d("abc", "------------------------> onDestroy...................");
        unsubscribe();
        stop();
        stopForeground(true);
        super.onDestroy();
    }

    public static Notification getCompatNotification(Context context, String msg) {
        String heading = context.getResources().getString(R.string.app_name);
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
/*
        if(Update.hasUpdate(context)!=0)
            heading+= " (Update Available)";
*/
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(heading).setContentText(msg).setContentIntent(pendingIntent);
        return builder.build();
    }

    void stop() {
        try {
            Log.d("abc", "phonesensor_Service running="+ServiceUtils.isServiceRunning(ServicePhoneSensor.class));
            if (ServiceUtils.isServiceRunning(ServicePhoneSensor.class)) {
                Intent intent = new Intent(this, ServicePhoneSensor.class);
                stopService(intent);
            }
        } catch (Exception e) {

        }

        try {
            isServiceRunning("abc");
            Log.d("abc", "motionsense_Service running="+ServiceUtils.isServiceRunning("org.md2k.motionsense.ServiceMotionSense"));
            Intent i = new Intent();
            i.setComponent(new ComponentName("org.md2k.motionsense", "org.md2k.motionsense.ServiceMotionSense"));
            stopService(i);
/*

            if (ServiceUtils.isServiceRunning("org.md2k.motionsense.ServiceMotionSense")) {
                Intent i = new Intent();
                i.setComponent(new ComponentName("org.md2k.motionsense", "org.md2k.motionsense.ServiceMotionSense"));
                stopService(i);
            }
*/
        } catch (Exception e) {
            Log.e("abc","failed to stop...motionsense");
        }
    }
    public boolean isServiceRunning(String abc) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                Log.d("service", service.service.getClassName());
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error checking service status", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    void start() {
        try {
            if (!ServiceUtils.isServiceRunning(ServicePhoneSensor.class)) {
                Intent intent = new Intent(this, ServicePhoneSensor.class);
                startService(intent);
            }
        } catch (Exception e) {

        }
        try {
            if (!ServiceUtils.isServiceRunning("org.md2k.motionsense.ServiceMotionSense")) {
                Intent i = new Intent();
                i.setComponent(new ComponentName("org.md2k.motionsense", "org.md2k.motionsense.ServiceMotionSense"));
                startService(i);
            }
        } catch (Exception e) {

        }
    }
}
