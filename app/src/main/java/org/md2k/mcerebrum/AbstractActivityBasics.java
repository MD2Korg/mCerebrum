package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.Utils;

import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.data.StudyInfo;
import org.md2k.mcerebrum.data.UserInfo;
import org.md2k.mcerebrum.internet.download.DownloadInfo;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG=AbstractActivityBasics.class.getSimpleName();
    public UserInfo userInfo;
    StudyInfo studyInfo;
    public ApplicationManager applicationManager;
    public ConfigManager configManager;
    Subscription subscription;
    MaterialDialog materialDialog;
    boolean isFirstTime;

    abstract void updateUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        configManager=new ConfigManager();
        studyInfo=new StudyInfo();
        userInfo=new UserInfo();
        applicationManager=new ApplicationManager();
        isFirstTime=true;
        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    finish();
                }else{
                    readConfig();
                }
            }
        });
    }
    @Override
    public void onResume(){
        if(!configManager.isConfigured()) {
            prepareConfig();
        }else if(isFirstTime){
            isFirstTime=false;
            updateUI();
        }
        super.onResume();
    }
    public boolean readConfig(){
        configManager.read();
        if(configManager.isConfigured()){
            userInfo.set();
            studyInfo.set(configManager.getConfig());
            applicationManager.set(configManager.getConfig().getApplications());
            return true;
        }else{
            userInfo.clear();
            studyInfo.clear();
            applicationManager.clear();
            return false;
        }
    }
    public void prepareConfig(){
        if(!readConfig()){
            downloadConfigDefault();
        }
    }
    void downloadConfigDefault(){
        materialDialog = Dialog.progress(this, "Loading configuration file...").show();
        subscription = configManager.downloadAndExtractDefault(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        updateUI();
                    }
                    @Override
                    public void onError(Throwable e) {
                        configManager.loadFromAsset(getBaseContext());
                        materialDialog.dismiss();
                        updateUI();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
//                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Handle Code
    }
    @Override
    public void onDestroy(){
        if(materialDialog!=null)
            materialDialog.dismiss();
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }

}

