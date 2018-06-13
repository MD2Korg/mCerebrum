package org.md2k.mcerebrum;
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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;

import org.md2k.mcerebrum.UI.app_install_uninstall.ActivityAppInstall;
import org.md2k.mcerebrum.cerebral_cortex.cerebralcortexwebapi.metadata.MetadataBuilder;
import org.md2k.mcerebrum.commons.permission.ActivityPermission;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppCP;
import org.md2k.mcerebrum.core.datakitapi.DataKitAPI;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeStringArray;
import org.md2k.mcerebrum.core.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.mcerebrum.core.datakitapi.source.application.Application;
import org.md2k.mcerebrum.core.datakitapi.source.application.ApplicationBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.source.platform.Platform;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformType;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.data.AppInfo;
import org.md2k.mcerebrum.data.MarkerInfo;
import org.md2k.mcerebrum.data.PlatformList;
import org.md2k.mcerebrum.datakit.ActivityClear;
import org.md2k.mcerebrum.phonesensor.ActivitySettings;
import org.md2k.mcerebrum.phonesensor.Configuration;
import org.md2k.mcerebrum.phonesensor.plot.ActivityPlot;
import org.md2k.mcerebrum.system.appinfo.AppInstall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.paperdb.Paper;

/**
 * Expose Java to JavaScript.
 */
class ActivityStarterModule extends ReactContextBaseJavaModule {

    private static DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = null;
    ActivityStarterModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();

        eventEmitter = getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from JavaScript.
     */
    @Override
    public String getName() {
        return "ActivityStarter";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("MyEventName", "MyEventValue");
        return constants;
    }

    @ReactMethod
    void pluginSettings(String packageName) {
        if(packageName.equals("org.md2k.mcerebrum")) {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, ActivitySettings.class);
                activity.startActivity(intent);
            }
        }else{
            Intent intent = new Intent();
            Bundle b = new Bundle();
            intent.setComponent(new ComponentName(packageName, "org.md2k.motionsense.ActivitySettings"));
            getCurrentActivity().startActivity(intent);        }
    }
    @ReactMethod
    void plot(String a1, String a2, String b1, String b2, String c1, String c2) {
        Platform p = new PlatformBuilder().setType(b1).setId(b2).build();
        Application a = new ApplicationBuilder().setType(c1).setId(c2).build();
        DataSource dataSource = new DataSourceBuilder().setType(a1).setId(a2).setPlatform(p).setApplication(a).build();
        if(c2.equals("org.md2k.mcerebrum")) {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, ActivityPlot.class);
                intent.putExtra("datasourcetype", a1);
                activity.startActivity(intent);
            }
        }else{
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putParcelable(DataSource.class.getSimpleName(), dataSource);
            intent.putExtra(DataSource.class.getSimpleName(), dataSource);
            intent.putExtras(b);
            String packageName = dataSource.getApplication().getId();
            intent.setComponent(new ComponentName(packageName, "org.md2k.motionsense.plot.ActivityPlot"));
            getCurrentActivity().startActivity(intent);

//            AppAccess.report(MainApplication.getContext(), packageName);
        }
    }

    @ReactMethod
    void permission() {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, ActivityPermission.class);
                activity.startActivity(intent);
            }
    }


    @ReactMethod
    void dataCollection(boolean status) {
        Activity activity = getCurrentActivity();
        Log.d("abc","dataCollection...status="+status+"...activity = "+activity+" isServiceRunning="+ServiceUtils.isServiceRunning(ServiceDataCollection.class));
        if(activity==null) return;
        if(status){
            if(!ServiceUtils.isServiceRunning(ServiceDataCollection.class)) {
                Intent intent = new Intent(activity, ServiceDataCollection.class);
                activity.startService(intent);
            }
        }else{
            if(ServiceUtils.isServiceRunning(ServiceDataCollection.class)) {
                Intent intent = new Intent(activity, ServiceDataCollection.class);
                activity.stopService(intent);
            }

        }
    }
    @ReactMethod
    void isDataCollection(@Nonnull Callback callback) {
        Activity activity = getCurrentActivity();
        if(activity==null) return;
            if(ServiceUtils.isServiceRunning(ServiceDataCollection.class))
                callback.invoke(true);
            else callback.invoke(false);
    }

    @ReactMethod
    void pluginListener(boolean status) {
        Activity activity = getCurrentActivity();
        if(activity==null) return;
        if(status){
            if(!ServiceUtils.isServiceRunning(ServicePluginListener.class)) {
                Intent intent = new Intent(activity, ServicePluginListener.class);
                activity.startService(intent);
            }
        }else{
            Intent intent = new Intent(activity, ServicePluginListener.class);
            activity.stopService(intent);

        }
    }

    @ReactMethod
    void pluginInstall(String packageName) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, ActivityAppInstall.class);
            intent.putExtra("package_name",packageName);
            activity.startActivity(intent);
        }
    }
    @ReactMethod
    void pluginUnInstall(String packageName) {
        AppUtils.uninstallApp(packageName);
    }
    @ReactMethod
    void clearData() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, ActivityClear.class);
            activity.startActivity(intent);
        }
    }

    @ReactMethod
    void dialNumber(@Nonnull String number) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            activity.startActivity(intent);
        }
    }

    @ReactMethod
    void getActivityName(@Nonnull Callback callback) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            callback.invoke(activity.getClass().getSimpleName());
        }
    }

    @ReactMethod
    void getActivityNameAsPromise(@Nonnull Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            promise.resolve(activity.getClass().getSimpleName());
        }
    }

/*
    @ReactMethod
    void callJavaScript() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            MainApplication application = (MainApplication) activity.getApplication();
            ReactNativeHost reactNativeHost = application.getReactNativeHost();
            ReactInstanceManager reactInstanceManager = reactNativeHost.getReactInstanceManager();
            ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
            Toast.makeText(activity,"callJavaScript"+reactContext, Toast.LENGTH_SHORT).show();

            if (reactContext != null) {
                CatalystInstance catalystInstance = reactContext.getCatalystInstance();
                WritableNativeArray params = new WritableNativeArray();
                params.pushString("Hello, JavaScript!");
                catalystInstance.callFunction("JavaScriptVisibleToJava", "alert", params);
            }
        }
    }

*/
    /**
     * To pass an object instead of a simple string, create a {@link WritableNativeMap} and populate it.
     */
    static void triggerAlert(@Nonnull String message) {
        eventEmitter.emit("MyEventValue", message);
    }
    @ReactMethod
    public void getPackageList(Callback successCallback) {
        try {
            Context c = getReactApplicationContext();
            ArrayList<String> pList = AppBasicInfo.get(c);
            ArrayList<AppInfo> aList=new ArrayList<>();
            for(int i =0;i<pList.size();i++){
                String p=pList.get(i);
                if(p.equals("org.md2k.mcerebrum")){
                    AppInfo a = new AppInfo(pList.get(i), AppBasicInfo.getTitle(c, p), AppBasicInfo.getSummary(c,p), true, Configuration.isConfigured());
                    aList.add(a);
                }else{
                    SharedPreferences sharedpreferences;
                    sharedpreferences = getCurrentActivity().getSharedPreferences("Mine", Context.MODE_PRIVATE);
                    boolean isInstalled = AppInstall.getInstalled(c,p);
                    if(!isInstalled){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("permission", false);
                        editor.apply();

                        AppInfo a = new AppInfo(pList.get(i), AppBasicInfo.getTitle(c, p), AppBasicInfo.getSummary(c,p), false, false);
                        AppCP.setPermissionOk(c,p,false);
                        aList.add(a);
                    }else{
                        boolean permission = sharedpreferences.getBoolean("permission",false);
                        if(permission==false){
                            String packageName = "org.md2k.motionsense";
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(packageName, "org.md2k.motionsense.permission.ActivityPermission"));
                            getCurrentActivity().startActivity(intent);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("permission", true);
                            editor.apply();
                        }

//                        BroadCastMessage.send(MainApplication.getContext(), MCEREBRUM.APP_ACCESS.APPCP_CHANGED);
                        AppInfo a = new AppInfo(pList.get(i), AppBasicInfo.getTitle(c, p), AppBasicInfo.getSummary(c,p), true, AppAccess.getConfigured(c,p));
                        aList.add(a);
                    }
                }
            }
            Gson gson=new Gson();
            String s = gson.toJson(aList);
            successCallback.invoke(s);
        } catch (Exception e) {
            Log.e("abc","error e = "+e);
        }
    }
    private ArrayList<MarkerInfo> readMarker(){
        ArrayList<MarkerInfo> list = new ArrayList<>();
        try {
            String filePath = Storage.getRootDirectory(getReactApplicationContext(), StorageType.SDCARD_INTERNAL) + "/mCerebrum/org.md2k.mcerebrum/marker.json";
            list = Storage.readJsonArrayList(filePath, MarkerInfo.class);
        }catch (Exception e){
        }
        return list;

    }
    @ReactMethod
    public void readMarker(Callback successCallback) {
            ArrayList<MarkerInfo> list=readMarker();
            Gson gson=new Gson();
            String s = gson.toJson(list);
            successCallback.invoke(s);
    }
    @ReactMethod
    public void addMarker(String title, String button1, String button2, Callback successCallback) {
        try {
            ArrayList<MarkerInfo> list=readMarker();
            list.add(new MarkerInfo(title, button1, button2));
            String filePath = Storage.getRootDirectory(getReactApplicationContext(), StorageType.SDCARD_INTERNAL)+"/mCerebrum/org.md2k.mcerebrum/marker.json";
            Storage.writeJsonArray(filePath, list);
            Gson gson=new Gson();
            String s = gson.toJson(list);
            successCallback.invoke(s);

        } catch (Exception e) {
            Log.e("abc","error e = "+e);
        }
    }
    @ReactMethod
    public void deleteMarker(String title, Callback successCallback) {
        try {
            ArrayList<MarkerInfo> list=readMarker();
            for(int i=0;i<list.size();i++)
                if(list.get(i).getTitle().equals(title)) {
                    list.remove(i);
                    break;
                }
            String filePath = Storage.getRootDirectory(getReactApplicationContext(), StorageType.SDCARD_INTERNAL)+"/mCerebrum/org.md2k.mcerebrum/marker.json";
            Storage.writeJsonArray(filePath, list);
            Gson gson=new Gson();
            String s = gson.toJson(list);
            successCallback.invoke(s);

        } catch (Exception e) {
            Log.e("abc","error e = "+e);
        }
    }

    @ReactMethod
    public void getDataSourceInfo(final Callback successCallback) {
        try {
            final DataKitAPI dataKitAPI = DataKitAPI.getInstance(getReactApplicationContext());
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    try {
//                        ArrayList<DataSourceInfo> list=new ArrayList<>();
                        PlatformList platformList = new PlatformList();
                        ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(new DataSourceBuilder());
                        for(int i =0;i<dataSourceClients.size();i++){
//                            if(dataSourceClients.get(i).getDataSource().getType().contains("_SUMMARY_")) continue;
//                            DataStream d = metadataBuilder.buildDataStreamMetadata(Constants.USER_UUID, dataSourceClients.get(i));
                            DataSource ds = dataSourceClients.get(i).getDataSource();
//                            boolean p=getPlot(ds);
                            long count = getCount(dataSourceClients.get(i).getDs_id());
                            Log.d("abc","read: key="+dataSourceClients.get(i).getDs_id()+"datasourcetype="+dataSourceClients.get(i).getDataSource().getType()+" value="+count);
                            String x=null, y=null;
                            if(ds.getPlatform()!=null){
                                x=ds.getPlatform().getType();
                                y=ds.getPlatform().getId();
                            }
                            if(x==null){
                                x= PlatformType.PHONE;
                            }
                            platformList.setSample(x,y,ds.getType(), ds.getId(), count);
//                            list.add(new DataSourceInfo(i+1, d.getName(), p, ds.getType(), ds.getId(), x, y, ds.getApplication().getType(), ds.getApplication().getId(), count));
                        }
                        Gson gson=new Gson();
                        String s = gson.toJson(platformList.getActiveList());
                        successCallback.invoke(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            successCallback.invoke("");
            Log.e("abc","error e = "+e);
        }
    }
    @ReactMethod
    public void setMarker(final String title, final String button) {
        try {
            Log.d("abc","marker -> title="+title+" button="+button);
            final DataKitAPI dataKitAPI = DataKitAPI.getInstance(getReactApplicationContext());
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    try {
//                        ArrayList<DataSourceInfo> list=new ArrayList<>();
                        DataSourceBuilder d = new DataSourceBuilder().setType("MARKER").setPlatform(new PlatformBuilder().setType(PlatformType.PHONE).build());
                        DataSourceClient dsc = dataKitAPI.register(d);
                        DataTypeStringArray data = new DataTypeStringArray(DateTime.getDateTime(), new String[]{title, button});
                        dataKitAPI.insert(dsc, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("abc","error e = "+e);
        }
    }

    private long getCount(int id){
        try {
            long l = Paper.book().read(String.valueOf(id));
            return l;
        }catch (Exception e){
            return 0;
        }
//        return Paper.book().read(String.valueOf(id));
//        SharedPreferences sharedPreferences= MainApplication.getContext().getSharedPreferences("COUNT", MODE_PRIVATE);
//        return sharedPreferences.getLong(String.valueOf(id), 0);
    }
/*
    private boolean getPlot(DataSource d){
        if(d.getApplication().getId().equals("org.md2k.mcerebrum")){
            switch(d.getType()){
                case DataSourceType.ACCELEROMETER:
                case DataSourceType.GYROSCOPE:
                case DataSourceType.COMPASS:
                case DataSourceType.AMBIENT_LIGHT:
                case DataSourceType.PRESSURE:
                case DataSourceType.PROXIMITY:
                    return true;
                    default:return false;
            }
        }
        else if(d.getApplication().getId().equals("org.md2k.motionsense")) {
            switch(d.getType()){
                case DataSourceType.ACCELEROMETER:
                case DataSourceType.GYROSCOPE:
                case DataSourceType.SEQUENCE_NUMBER:
                case DataSourceType.LED:
                case DataSourceType.QUATERNION:
                case DataSourceType.MAGNETOMETER:
                    return true;
                default:return false;
            }
        }
        else return false;
    }
*/

}