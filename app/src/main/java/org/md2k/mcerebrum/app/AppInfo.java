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
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.configuration.CApp;
import org.md2k.mcerebrum.core.access.Info;

import java.io.IOException;

public class AppInfo implements Parcelable{
    private static final String STATUS_REQUIRED="REQUIRED";
    private static final String STATUS_OPTIONAL="OPTIONAL";
    private static final String STATUS_NOT_IN_USE="NOT_IN_USE";
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String packageName;
    private String icon;
    private String downloadFromGithub;
    private String downloadFromPlayStore;
    private String downloadFromURL;
    private String expectedVersion;
    private String updateOption;
    private String updateVersionName;
    private String currentVersionName;
    private int currentVersionCode;
    private boolean installed;
    private String status;
    private boolean mCerebrumSupported;
    private Info info;

    AppInfo(CApp capp) {
        id = capp.getId();
        type = capp.getType();
        title = capp.getTitle();
        summary = capp.getSummary();
        description = capp.getDescription();
        packageName = capp.getPackage_name();
        icon = capp.getIcon();
        downloadFromGithub = capp.getDownload_from_github();
        downloadFromPlayStore = capp.getDownload_from_playstore();
        downloadFromURL = capp.getDownload_from_url();
        expectedVersion = capp.getVersion();
        updateOption = capp.getUpdate();
        status = capp.getStatus();
        setInstalled();
        setmCerebrum(null);
    }

    protected AppInfo(Parcel in) {
        id = in.readString();
        type = in.readString();
        title = in.readString();
        summary = in.readString();
        description = in.readString();
        packageName = in.readString();
        icon = in.readString();
        downloadFromGithub = in.readString();
        downloadFromPlayStore = in.readString();
        downloadFromURL = in.readString();
        expectedVersion = in.readString();
        updateOption = in.readString();
        updateVersionName = in.readString();
        currentVersionName = in.readString();
        currentVersionCode = in.readInt();
        installed = in.readByte() != 0;
        status = in.readString();
        mCerebrumSupported = in.readByte() != 0;
        info = in.readParcelable(Info.class.getClassLoader());
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    void setInstalled(){
        installed = AppUtils.isInstallApp(packageName);
        if (installed) {
            currentVersionName = AppUtils.getAppVersionName(packageName);
            currentVersionCode = AppUtils.getAppVersionCode(packageName);
        }
    }
    void setmCerebrum(Info info){
        this.info = info;
    }



    int getCurrentVersionCode() {
        return currentVersionCode;
    }

    public String getCurrentVersionName() {
        return currentVersionName;
    }

    boolean isUpdateAvailable() {
        if (updateVersionName == null) return false;
        if (!isInstalled()) return false;
        if (getCurrentVersionName().equals(updateVersionName)) return false;
        return true;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getIcon(Context context) {
        if (isInstalled()) return AppUtils.getAppIcon(packageName);
        else {
            try {
                if (icon != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(Constants.CONFIG_MCEREBRUM_DIR()+icon);
                    if (bitmap != null)
                        return new BitmapDrawable(context.getResources(), bitmap);
                }
            } catch (Exception ignored) {

            }
            AssetManager am = context.getAssets();
            try {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mcerebrum.png")));
            } catch (IOException ignored) {
            }
            return null;

        }
    }
    public String getDownloadFromGithub() {
        return downloadFromGithub;
    }

    public String getDownloadFromPlayStore() {
        return downloadFromPlayStore;
    }

    public String getDownloadFromURL() {
        return downloadFromURL;
    }

    String getExpectedVersion() {
        return expectedVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isInstalled() {
        return installed;
    }

/*
    public void updateInfo() {
        boolean res=AppUtils.isInstallApp(packageName);
        if(installed!=res) {
            installed =res;
            if (installed) {
                currentVersionName = AppUtils.getAppVersionName(packageName);
                currentVersionCode = AppUtils.getAppVersionCode(packageName);
                startService();
            }else
                stopService();
        }
    }
*/

/*
    public void updateStatus() {
        try {
            Info info = serviceCommunication.getInfo();
            isConfigurable = info.isConfigurable();
            runningTime = info.getRunningTime();
            runInBackground = info.isRunInBackground();
            configured = info.isConfigured();
            report = info.hasReport();
            running = info.isRunning();
        }catch (Exception e){

        }
    }
*/

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public String getUpdateOption() {
        return updateOption;
    }

    public String getUpdateVersionName() {
        return updateVersionName;
    }

    public boolean isRequired(){
        if(status==null) return false;
        if(STATUS_REQUIRED.equals(status.toUpperCase())) return true;
        return false;
    }
    public boolean isOptional(){
        if(status==null) return true;
        if(STATUS_OPTIONAL.equals(status.toUpperCase())) return true;
        return false;
    }
    public boolean isNotInUse(){
        if(status==null) return false;
        if(STATUS_NOT_IN_USE.equals(status.toUpperCase())) return true;
        return false;
    }
    public String getStatus(){
        if(isRequired()) return "Required";
        else if(isNotInUse()) return "Not in use";
        else return "Optional";
    }
    public void setmCerebrumSupported(boolean result){
        mCerebrumSupported=result;
    }

    public boolean isMCerebrumSupported() {
        return mCerebrumSupported;
    }

    public void launch(Activity activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        activity.startActivity( intent );
    }

    public Info getInfo() {
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(description);
        dest.writeString(packageName);
        dest.writeString(icon);
        dest.writeString(downloadFromGithub);
        dest.writeString(downloadFromPlayStore);
        dest.writeString(downloadFromURL);
        dest.writeString(expectedVersion);
        dest.writeString(updateOption);
        dest.writeString(updateVersionName);
        dest.writeString(currentVersionName);
        dest.writeInt(currentVersionCode);
        dest.writeByte((byte) (installed ? 1 : 0));
        dest.writeString(status);
        dest.writeByte((byte) (mCerebrumSupported ? 1 : 0));
        dest.writeParcelable(info, flags);
    }
}
