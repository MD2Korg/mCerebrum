package org.md2k.mcerebrum.UI.check_update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.core.internet.download.DownloadInfo;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.system.appinfo.AppInstall;
import org.md2k.system.appinfo.BroadCastMessage;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;

public class ActivityCheckUpdate extends AppCompatActivity {
    Subscription subscription;
    Subscription subscriptionUpdate;
    int installAllIndex = -1;
    boolean res=false;
    boolean isUpdateUI = false;
    ArrayList<String> packageNames;

    MaterialDialog materialDialog;
    ActivityMain activityMain;
    boolean hasUpdate;
    String studyPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studyPackageName = getIntent().getStringExtra("STUDY");
        setContentView(R.layout.activity_check_update);
        packageNames=AppBasicInfo.get(this);
        BroadCastMessage.send(this, MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
        createInstallListener();
        materialDialog = new MaterialDialog.Builder(this)
                .content("Checking updates ...")
                .progress(true, 100, false)
                .show();
        subscription = Observable.merge(ConfigManager.checkUpdate(this), AppInstall.checkUpdate(this))
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean == true;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        if (!hasUpdate) {
                            Toasty.success(ActivityCheckUpdate.this, "up-to-date", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Dialog.simple(ActivityCheckUpdate.this, "Update Available", "Do you want to update?", "Yes", "Cancel", new DialogCallback() {
                                @Override
                                public void onSelected(String value) {
                                    if ("Yes".equals(value)) {
                                        subscriptionUpdate = ConfigManager.checkUpdate(ActivityCheckUpdate.this).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                                                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                                                    @Override
                                                    public Observable<Boolean> call(Boolean aBoolean) {
                                                        if (aBoolean == true)
                                                            return ConfigManager.updateConfigServer(MyApplication.getContext(), Constants.CONFIG_ROOT_DIR())
                                                                    .subscribeOn(Schedulers.newThread())
                                                                    .observeOn(AndroidSchedulers.mainThread()).map(new Func1<Boolean, Boolean>() {
                                                                        @Override
                                                                        public Boolean call(Boolean aBoolean) {
                                                                            return null;
                                                                        }
                                                                    });

                                                        return Observable.just(true);
                                                    }
                                                }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
                                                    @Override
                                                    public Observable<Boolean> call(Boolean aBoolean) {
                                                        return AppInstall.checkUpdate(ActivityCheckUpdate.this);
                                                    }
                                                }).subscribe(new Observer<Boolean>() {
                                                    @Override
                                                    public void onCompleted() {
                                                        if(res==true){
                                                            installAllIndex = 0;
                                                            downloadAndInstallAll();

                                                        }else{
                                                            activityMain.responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        activityMain.responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
                                                    }

                                                    @Override
                                                    public void onNext(Boolean aBoolean) {
                                                        res=res | aBoolean;
                                                    }
                                                });

                                    } else {
                                        finish();
                                    }
                                }
                            }).cancelable(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityCheckUpdate.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        hasUpdate = true;

                    }
                });

    }
    void installMCerebrumIfRequired() {
        String p = AppBasicInfo.getMCerebrum(this);
        String cv=AppInstall.getCurrentVersion(this, p);
        String lv=AppInstall.getLatestVersion(this, p);
        if(cv.equals(lv)){
            installAllIndex=-1;
            finish();
        }
        install(p);
        installAllIndex = -1;
    }
    void install(String packageName) {

        materialDialog = new MaterialDialog.Builder(this)
                .content("Downloading " + AppBasicInfo.getTitle(this, packageName) + " ...")
                .progress(false, 100, true)
                .show();
        subscription = AppInstall.install(this, packageName)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        BroadCastMessage.send(ActivityCheckUpdate.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityCheckUpdate.this, "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc", "total=" + downloadInfo.getTotalFileSize() + "downloaded=" + downloadInfo.getCurrentFileSize() + " progressWithBar=" + downloadInfo.getProgress());
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }
    void downloadAndInstallAll() {

        if (installAllIndex == -1) {
            finish();
            return;
        }
        while (installAllIndex < packageNames.size()) {
            if (!AppInstall.hasUpdate(this, packageNames.get(installAllIndex)) || AppBasicInfo.getMCerebrum(this).equals(packageNames.get(installAllIndex)))
                installAllIndex++;
            else break;
        }
        if (installAllIndex >= packageNames.size()) {
            installMCerebrumIfRequired();
            return;
        }
        install(packageNames.get(installAllIndex));
        installAllIndex++;
/*
        downloadAndInstall(applicationManager.get()[installAllIndex].getInstallInfoController());
        applicationManager.get()[installAllIndex].getInstallInfoController().install(getActivity());
*/
/*
        if(applicationManager.get()[installAllIndex].getDownloadFromGithub()!=null || applicationManager.getAppInfos()[installAllIndex].getDownloadFromURL()!=null){
            downloadAndInstall(applicationManager.getAppInfos()[installAllIndex]);
        }
        else new AppInstall(applicationManager.getAppInfos()[installAllIndex]).install(getActivity());
*/
    }

    @Override
    public void onResume() {
        if (isUpdateUI) {
            downloadAndInstallAll();
            isUpdateUI = false;
        }
        super.onResume();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(br);
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        if (subscriptionUpdate != null && !subscriptionUpdate.isUnsubscribed())
            subscriptionUpdate.unsubscribe();
        if(studyPackageName!=null){
            AppAccess.launch(this, studyPackageName);
        }
        super.onDestroy();
    }
    void createInstallListener(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);
    }
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
//                case Intent.ACTION_PACKAGE_CHANGED:
//                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    String[] temp = intent.getData().toString().split(":");
                    String packageName;
                    if (temp.length == 1)
                        packageName = temp[0];
                    else if (temp.length == 2)
                        packageName = temp[1];
                    else packageName = "";
                    AppInstall.set(getApplicationContext(), packageName);
                    isUpdateUI = true;
                    break;
            }
        }
    };

}
