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
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.data.MySharedPreference;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;
import org.md2k.mcerebrum.internet.github.service.Github;

import rx.Observable;
import rx.functions.Func1;

import static com.mikepenz.iconics.Iconics.TAG;

public class Application {
    private static final String ID="ID";
    private static final String TYPE="TYPE";
    private static final String TITLE="TITLE";
    private static final String SUMMARY="SUMMARY";
    private static final String DESCRIPTION="DESCRIPTION";
    private static final String PACKAGE_NAME="PACKAGE_NAME";
    private static final String ICON="ICON";
    private static final String DOWNLOAD_FROM_GITHUB="DOWNLOAD_FROM_GITHUB";
    private static final String DOWNLOAD_FROM_PLAYSTORE="DOWNLOAD_FROM_PLAYSTORE";
    private static final String DOWNLOAD_FROM_URL="DOWNLOAD_FROM_URL";
    private static final String EXPECTED_VERSION="EXPECTED_VERSION";
    private static final String UPDATE="UPDATE";

    private String packageName;
    Application(String packageName){
        this.packageName=packageName;
    }
    private static String getKey(String packageName, String key){
        return Application.class.getSimpleName()+"_"+packageName+"_"+key;
    }


    static void save(Context context, CApp cApp){
        String p=cApp.getPackage_name();
        new MySharedPreference().set(context,getKey(p, ID),cApp.getId());
        new MySharedPreference().set(context,getKey(p, TYPE),cApp.getType());
        new MySharedPreference().set(context,getKey(p, TITLE),cApp.getTitle());
        new MySharedPreference().set(context,getKey(p, SUMMARY),cApp.getSummary());
        new MySharedPreference().set(context,getKey(p, DESCRIPTION),cApp.getDescription());
        new MySharedPreference().set(context,getKey(p, PACKAGE_NAME),cApp.getPackage_name());
        new MySharedPreference().set(context,getKey(p, DOWNLOAD_FROM_GITHUB),cApp.getDownload_from_github());
        new MySharedPreference().set(context,getKey(p, DOWNLOAD_FROM_PLAYSTORE),cApp.getDownload_from_playstore());
        new MySharedPreference().set(context,getKey(p, DOWNLOAD_FROM_URL),cApp.getDownload_from_url());
        new MySharedPreference().set(context,getKey(p, UPDATE),cApp.getUpdate());
        new MySharedPreference().set(context,getKey(p, ICON),cApp.getIcon());
        new MySharedPreference().set(context,getKey(p, EXPECTED_VERSION),cApp.getVersion());
    }


    Observable<DownloadInfo> downloadURL(Context context, String filePath, String fileName){
        if(getDownloadFromGithub(context)==null) return null;
            return new DownloadFile().download(getDownloadFromUrl(context), filePath, fileName);
    }
    Observable<DownloadInfo> downloadLatest(final Context context, final String filePath, final String fileName){
        if(getDownloadFromGithub(context)==null) return null;
        Observable<DownloadInfo> downloadInfoObservable=getLatestVersion(context).map(new Func1<ReleaseInfo, String>() {
            @Override
            public String call(ReleaseInfo releaseInfo) {
                for(int i=0;i<releaseInfo.getAssets().length;i++) {
                    if(releaseInfo.getAssets()[i].getBrowser_download_url().endsWith(".apk"))
                            return releaseInfo.getAssets()[i].getBrowser_download_url();
                }
                return null;
            }
        }).flatMap(new Func1<String, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(String s) {
                if(s==null) return null;
                return new DownloadFile().download(getDownloadFromUrl(context), filePath, fileName);
            }
        });
        return new DownloadFile().download(getDownloadFromUrl(context), filePath, fileName);
    }

    public Observable<ReleaseInfo[]> getVersions(Context context){
        if(getDownloadFromGithub(context)==null) return null;
        String[] parts=getDownloadFromGithub(context).split("/");
        if(parts.length!=2) return null;
        Github github=new Github();
        return github.getReleases(parts[0], parts[1]);
    }
    public Observable<ReleaseInfo> getLatestVersion(Context context){
        if(getDownloadFromGithub(context)==null) return null;
        String[] parts=getDownloadFromGithub(context).split("/");
        if(parts.length!=2) return null;
        Github github=new Github();
        return github.getReleaseLatest(parts[0], parts[1]);
    }
    void install(Activity activity, String filePath , int requestCode){
        AppUtils.installApp(activity,filePath, "org.md2k.mcerebrum.provider",requestCode);
    }
    public boolean isInstalled(){
        Log.d("abc","packageName="+packageName);
        return AppUtils.isInstallApp(packageName);
    }
    void uninstall(Activity activity, int requestCode){
        AppUtils.uninstallApp(activity, packageName, requestCode);
    }
    int getVersionCode(){
        return AppUtils.getAppVersionCode(packageName);
    }
    public String getVersionName(){
        return AppUtils.getAppVersionName(packageName);
    }
    boolean isUpdateAvailable(Context context){
        return isInstalled() && !getVersionName().equals(getExpectedVersion(context));
    }
    Drawable getIcon(Context context, String filePath){
        if(isInstalled()) return AppUtils.getAppIcon(packageName);
        else{
            String imageInSD = filePath+"/"+getIcon(context);
            Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
            return new BitmapDrawable(context.getResources(), bitmap);
        }
    }
    private String get(Context context, String key){
        return new MySharedPreference().getString(context, getKey(packageName, key), null);
    }
    private String getId(Context context) {
        return get(context, ID);
    }
    private String getType(Context context) {
        return get(context, TYPE);
    }
    public String getTitle(Context context) {
        return get(context, TITLE);
    }
    public String getSummary(Context context) {
        return get(context, SUMMARY);
    }
    public String getDescription(Context context) {
        return get(context, DESCRIPTION);
    }
    public String getIcon(Context context) {
        return get(context, ICON);
    }
    private String getDownloadFromGithub(Context context) {
        return get(context, DOWNLOAD_FROM_GITHUB);
    }
    private String getDownloadFromPlaystore(Context context) {
        return get(context, DOWNLOAD_FROM_PLAYSTORE);
    }
    private String getDownloadFromUrl(Context context) {
        return get(context, DOWNLOAD_FROM_URL);
    }
    private String getExpectedVersion(Context context) {
        return get(context, EXPECTED_VERSION);
    }
    private String getUpdate(Context context) {
        return get(context, UPDATE);
    }
}
