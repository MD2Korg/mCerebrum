package org.md2k.mcerebrum;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.core.access.SampleProvider;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppInfoColumns;
import org.md2k.mcerebrum.system.appinfo.AppCPObserver;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ServicePluginListener extends Service {
    AppCPObserver appCPObserver;
    public ServicePluginListener() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        if (Permission.hasPermission(this)) {
            ConfigManager.load(getApplicationContext(), ConfigManager.LOAD_TYPE.READ);
            appCPObserver = new AppCPObserver(this, new Handler());
            getContentResolver().
                    registerContentObserver(
                            Uri.parse(SampleProvider.CONTENT_URI_BASE + "/" + AppInfoColumns.TABLE_NAME),
                            true,
                            appCPObserver);
            initStart();
        }


    }
    public void initStart(){
        Log.d("abc","initStart...");
        ArrayList<String> packageNames= AppBasicInfo.get(this);
        for(int in=0;in<packageNames.size();in++) {
            String packageName =packageNames.get(in);
            try {
                Intent i = new Intent();
                i.setComponent(new ComponentName(packageName, "org.md2k.mcerebrum.core.access.ActivityEmpty"));
                startActivity(i);
            } catch (Exception ignored) {
            }
        }
        Observable.timer(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread()).map(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                BroadCastMessage.send(ServicePluginListener.this);
                return true;
            }
        }).subscribe();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
