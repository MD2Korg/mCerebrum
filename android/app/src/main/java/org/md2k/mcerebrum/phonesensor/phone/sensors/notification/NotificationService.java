package org.md2k.mcerebrum.phonesensor.phone.sensors.notification;

/**
 * Created by akanes on 10/10/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.md2k.mcerebrum.core.datakitapi.DataKitAPI;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeString;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.METADATA;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformBuilder;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationService extends NotificationListenerService {


    private String TAG = "abc";
    boolean isRecord=false;


    DataKitAPI dataKitAPI;
    Context context;


    // bind and unbind seems to make it work with Android 6...
    // but is never called with Android 4.4...
    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder mIBinder = super.onBind(mIntent);
        Log.i(TAG, "onBind");
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent mIntent) {
        boolean mOnUnbind = super.onUnbind(mIntent);
        Log.i(TAG, "onUnbind");
        //isNotificationAccessEnabled = false;
        try {
        } catch (Exception e) {
            Log.e(TAG, "Error during unbind", e);
        }
        return mOnUnbind;
    }

    // onCreate is called with Android 4.4
    // because the service is explicitly started from the MainActivity.
    // Not on Android 6 where the system binds this service itself...

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "**********  onCreate");
        context = getApplicationContext();
        isRecord = true;
    }

    @Override
    public void onDestroy() {
        isRecord = false;
//        disconnectDataKit();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG, "onNotificationPosted");
        final String packagename = sbn.getPackageName();
        if(!isRecord) return;

        dataKitAPI = DataKitAPI.getInstance(context);
        try {
            //DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("CU_NOTIF_POST_TICKERTEXT");
            //dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.FREQUENCY ,"ON_CHANGE").setMetadata(METADATA.DESCRIPTION,"ticker text of posted notification (Text that summarizes the notification for accessibility services)").setMetadata(METADATA.NAME,"Notification post tickertext").setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeString");
            //DataSourceClient dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            //DataTypeString NOTIF_POST_TICKERTEXT = new DataTypeString(DateTime.getDateTime(), tickerText);

            //dataKitAPI.insert(dataSourceClient, NOTIF_POST_TICKERTEXT);
            //Log.i(TAG, "Notification Post Tickertext");
            HashMap<String, String> dataDescriptor2 = new HashMap<>();
            dataDescriptor2.put(METADATA.NAME, "Notification post package");
            dataDescriptor2.put(METADATA.DESCRIPTION, "App package that posted a notification");
            dataDescriptor2.put(METADATA.FREQUENCY, "ON_CHANGE");
            dataDescriptor2.put(METADATA.UNIT, "package_name");
            dataDescriptor2.put(METADATA.DATA_TYPE, "String");
            ArrayList<HashMap<String, String>> dss2 = new ArrayList<>();
            dss2.add(dataDescriptor2);

            DataSourceBuilder dataSourceBuilder2 = new DataSourceBuilder().setType(DataSourceType.CU_NOTIF_POST_PACKAGE);
            dataSourceBuilder2 = dataSourceBuilder2
                    .setMetadata(METADATA.NAME, "Notification post package")
                    .setMetadata(METADATA.DESCRIPTION, "App package that posted a notification")
                    .setMetadata(METADATA.FREQUENCY, "ON_CHANGE")
                    .setMetadata(METADATA.UNIT, "package_name")
                    .setMetadata(METADATA.DATA_TYPE, "org.md2k.datakitapi.datatype.DataTypeString");
            dataSourceBuilder2 = dataSourceBuilder2.setDataDescriptors(dss2);

            DataSourceClient dataSourceClient2 = dataKitAPI.register(dataSourceBuilder2);
            Log.d("abc", "post_notif id=" + dataSourceClient2.getDs_id() + " dataSourceType=" + dataSourceClient2.getDataSource().getType()+" status="+dataSourceClient2.getStatus().getStatusCode());
            DataTypeString NOTIF_POST_PACKAGE = new DataTypeString(DateTime.getDateTime(), packagename);

            Log.i(TAG, "Notification Post package packageName = " + packagename);
            dataKitAPI.insert(dataSourceClient2, NOTIF_POST_PACKAGE);
            Log.i(TAG, "Notification Post package packageName = " + packagename + " ... after insert");
        } catch (Exception e) {
            Log.e("abc", "error...e=" + e.getMessage());
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(!isRecord) return;
        Log.i(TAG, "onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        final String packagename = sbn.getPackageName();


        dataKitAPI = DataKitAPI.getInstance(context);
        try {
            //DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("CU_NOTIF_RM_TICKERTEXT");
            //dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.FREQUENCY ,"ON_CHANGE").setMetadata(METADATA.DESCRIPTION,"Ticker text of removed notification").setMetadata(METADATA.NAME,"Notification remove tickertext").setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeString");
            //DataSourceClient dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            //DataTypeString NOTIF_RM_TICKERTEXT = new DataTypeString(DateTime.getDateTime(), tickerText);

            //dataKitAPI.insert(dataSourceClient, NOTIF_RM_TICKERTEXT);
            //Log.i(TAG, "Notification RM Tickertext");
            HashMap<String, String> dataDescriptor2 = new HashMap<>();
            dataDescriptor2.put(METADATA.NAME, "Notification remove package");
            dataDescriptor2.put(METADATA.DESCRIPTION, "App package that removed a notification");
            dataDescriptor2.put(METADATA.FREQUENCY, "ON_CHANGE");
            dataDescriptor2.put(METADATA.UNIT, "package_name");
            dataDescriptor2.put(METADATA.DATA_TYPE, "String");
            ArrayList<HashMap<String, String>> dss2 = new ArrayList<>();
            dss2.add(dataDescriptor2);

            DataSourceBuilder dataSourceBuilder2 = new DataSourceBuilder().setType(DataSourceType.CU_NOTIF_RM_PACKAGE);
            dataSourceBuilder2 = dataSourceBuilder2
                    .setMetadata(METADATA.NAME, "Notification remove package")
                    .setMetadata(METADATA.DESCRIPTION, "App package that removed a notification")
                    .setMetadata(METADATA.FREQUENCY, "ON_CHANGE")
                    .setMetadata(METADATA.UNIT, "package_name")
                    .setMetadata(METADATA.DATA_TYPE, "org.md2k.datakitapi.datatype.DataTypeString");
            dataSourceBuilder2 = dataSourceBuilder2.setDataDescriptors(dss2);

            DataSourceClient dataSourceClient2 = dataKitAPI.register(dataSourceBuilder2);
            DataTypeString NOTIF_RM_PACKAGE = new DataTypeString(DateTime.getDateTime(), packagename);

            dataKitAPI.insert(dataSourceClient2, NOTIF_RM_PACKAGE);
            Log.i(TAG, "Notification RM package");


        } catch (DataKitException e) {
            e.printStackTrace();
            Log.i(TAG, "Notification RM datakit Error");
        }
    }

}

