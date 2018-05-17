package org.md2k.mcerebrum.UI.check_update;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;
import org.md2k.mcerebrum.system.update.Update;

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
    Subscription subscriptionInstall;
    MaterialDialog materialDialogInstall;
    int installAllIndex = -1;
    boolean res=false;
    boolean isUpdateUI = false;
    ArrayList<String> packageNames;

    MaterialDialog materialDialog;
    boolean hasUpdate;
    String studyPackageName;
    private boolean updateYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateYes=false;
        studyPackageName = getIntent().getStringExtra("STUDY");
        setContentView(R.layout.activity_check_update);
        packageNames=AppBasicInfo.get(this);
        BroadCastMessage.send(this, MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
        createInstallListener();
        materialDialog = new MaterialDialog.Builder(this)
                .content("Checking updates ...")
                .progress(true, 100, false)
                .show();
        subscription = Observable.just(true).subscribeOn(Schedulers.newThread()).flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return Update.checkUpdate(ActivityCheckUpdate.this).observeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        if (!hasUpdate) {
                            Toasty.success(ActivityCheckUpdate.this, "up-to-date", Toast.LENGTH_SHORT).show();
                            updateYes=false;
                            finish();
                        } else {
                            Dialog.simple(ActivityCheckUpdate.this, "Update Available", "Do you want to update?", "Yes", "Cancel", new DialogCallback() {
                                @Override
                                public void onSelected(String value) {
                                    if ("Yes".equals(value)) {
                                        updateYes=true;
                                        subscriptionUpdate = Update.checkUpdateServer(ActivityCheckUpdate.this).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                                                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                                                    @Override
                                                    public Observable<Boolean> call(Boolean aBoolean) {
                                                        if (aBoolean) {
                                                            return ConfigManager.updateConfigServer(MyApplication.getContext(), Constants.CONFIG_ROOT_DIR())
                                                                    .subscribeOn(Schedulers.newThread())
                                                                    .observeOn(AndroidSchedulers.mainThread()).map(new Func1<Boolean, Boolean>() {
                                                                        @Override
                                                                        public Boolean call(Boolean aBoolean) {
                                                                            return true;
                                                                        }
                                                                    });
                                                        }
                                                        else return Observable.just(true);
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
                                                            updateYes=false;
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        updateYes=false;
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onNext(Boolean aBoolean) {
                                                        res=res | aBoolean;
                                                    }
                                                });

                                    } else {
                                        updateYes=false;
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
                        updateYes=false;
                        finish();
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
            BroadCastMessage.send(this, MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
            updateYes=false;
            finish();
        }
        install(p);
    }
    void install(String packageName) {

        materialDialogInstall = new MaterialDialog.Builder(this)
                .content("Downloading " + AppBasicInfo.getTitle(this, packageName) + " ...")
                .progress(false, 100, true)
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        installAllIndex=-1;
                        if(subscriptionInstall!=null && !subscriptionInstall.isUnsubscribed())
                            subscriptionInstall.unsubscribe();
                        if(materialDialogInstall!=null && materialDialogInstall.isShowing()) materialDialogInstall.dismiss();
                    }
                })
                .show();
        subscriptionInstall = AppInstall.install(this, packageName)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialogInstall.dismiss();
                        BroadCastMessage.send(ActivityCheckUpdate.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityCheckUpdate.this, "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialogInstall.dismiss();
                        updateYes=false;
                        finish();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc", "total=" + downloadInfo.getTotalFileSize() + "downloaded=" + downloadInfo.getCurrentFileSize() + " progressWithBar=" + downloadInfo.getProgress());
                        materialDialogInstall.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }
    void downloadAndInstallAll() {

        if (installAllIndex == -1) {

            BroadCastMessage.send(ActivityCheckUpdate.this, MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
            initStart();
            updateYes=false;
            finish();
            return;
        }
        installAllIndex=0;
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
        if (isUpdateUI && updateYes) {
            isUpdateUI = false;
            downloadAndInstallAll();
        }
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(subscriptionInstall!=null && !subscriptionInstall.isUnsubscribed())
            subscriptionInstall.unsubscribe();
        if(materialDialogInstall!=null && materialDialogInstall.isShowing())materialDialogInstall.dismiss();
        isUpdateUI=true;
    }


    @Override
    public void onDestroy(){
        unregisterReceiver(br);
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        if (materialDialogInstall != null && materialDialogInstall.isShowing()) materialDialogInstall.dismiss();
        if (subscriptionInstall != null && !subscriptionInstall.isUnsubscribed())
            subscriptionInstall.unsubscribe();
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
                case Intent.ACTION_PACKAGE_CHANGED:
                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    String[] temp = intent.getData().toString().split(":");
                    String packageName;
                    if (temp.length == 1)
                        packageName = temp[0];
                    else if (temp.length == 2)
                        packageName = temp[1];
                    else packageName = "";
                    AppInstall.set(getApplicationContext(), packageName);
                    try {
                        Intent i = new Intent();
                        i.setComponent(new ComponentName(packageName, "org.md2k.mcerebrum.core.access.ActivityEmpty"));
                        startActivity(i);
                    }catch(Exception e){
                    }
                    if(installAllIndex==-1)
                        BroadCastMessage.send(ActivityCheckUpdate.this, MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
                    isUpdateUI = true;
                    break;
            }
        }
    };
    public void initStart(){
        ArrayList<String> packageNames=AppBasicInfo.get(this);
        for(int in=0;in<packageNames.size();in++) {
            String packageName =packageNames.get(in);
            try {
                Intent i = new Intent();
                i.setComponent(new ComponentName(packageName, "org.md2k.mcerebrum.core.access.ActivityEmpty"));
                startActivity(i);
            } catch (Exception ignored) {
            }
        }

    }

}
