package org.md2k.mcerebrum.UI.check_update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.MyApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.mcerebrum.update.Update;
import org.md2k.system.app.AppInfoController;
import org.md2k.system.app.ApplicationManager;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.internet.download.DownloadInfo;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_APP_ADD_REMOVE;
import static org.md2k.mcerebrum.menu.AbstractMenu.MENU_HOME;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FragmentCheckUpdate extends Fragment {
    Subscription subscription;
    Subscription subscriptionUpdate;
    int installAllIndex = -1;
    boolean res=false;
    boolean isUpdateUI = false;

    MaterialDialog materialDialog;
    DataManager dataManager;
    ActivityMain activityMain;
    boolean hasUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_check_update, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(br, intentFilter);
        activityMain = (ActivityMain) getActivity();
        dataManager = activityMain.dataManager;
        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Checking updates ...")
                .progress(true, 100, false)
                .show();

        subscription = Update.hasUpdate(MyApplication.getContext(), dataManager, dataManager.getApplicationManager())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        if (!hasUpdate) {
                            Toasty.success(getActivity(), "up-to-date", Toast.LENGTH_SHORT).show();
                            ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
                        } else {
                            Dialog.simple(activityMain, "Update Available", "Do you want to update?", "Yes", "Cancel", new DialogCallback() {
                                @Override
                                public void onSelected(String value) {
                                    if ("Yes".equals(value)) {
                                        subscriptionUpdate = Update.hasUpdateConfigServer(getActivity(), dataManager.getDataCPManager().getServerCP()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                                                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                                                    @Override
                                                    public Observable<Boolean> call(Boolean aBoolean) {
                                                        if (aBoolean == true)
                                                            return Update.updateConfigServer(dataManager, dataManager.getDataCPManager().getServerCP(), Constants.CONFIG_ROOT_DIR())
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
                                                        return Update.hasUpdateApp(dataManager.getApplicationManager());
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
                                        ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_HOME);
                                    }
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        hasUpdate = true;

                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
/*
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        if (subscriptionUpdate != null && !subscriptionUpdate.isUnsubscribed())
            subscriptionUpdate.unsubscribe();
*/
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        if (isUpdateUI) {
            downloadAndInstallAll();
            isUpdateUI = false;
        }
        super.onResume();
    }

    void installMCerebrumIfRequired() {
        int ind_mc = 0;
        for (int i = 0; i < dataManager.getApplicationManager().get().size(); i++) {
            if (dataManager.getApplicationManager().get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_MCEREBRUM)) {
                ind_mc = i;
                break;
            }
        }
        if (dataManager.getApplicationManager().get(ind_mc).getInstallInfoController().isInstalled() &&
                !dataManager.getApplicationManager().get(ind_mc).getInstallInfoController().hasUpdate()) {
            installAllIndex = -1;
            return;
        }
        install(dataManager.getApplicationManager().get(ind_mc));
        installAllIndex = -1;
    }

    void downloadAndInstallAll() {
        if (installAllIndex == -1) {
            ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MENU_APP_ADD_REMOVE);
            return;
        }
        while (installAllIndex < dataManager.getApplicationManager().get().size()
                && ((dataManager.getApplicationManager().get(installAllIndex).getInstallInfoController().isInstalled()
                && !dataManager.getApplicationManager().get().get(installAllIndex).getInstallInfoController().hasUpdate())
                || dataManager.getApplicationManager().get().get(installAllIndex).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_MCEREBRUM)))
            installAllIndex++;
        if (installAllIndex >= dataManager.getApplicationManager().get().size()) {
            installMCerebrumIfRequired();
            installAllIndex = -1;
            return;
        }
        install(dataManager.getApplicationManager().get().get(installAllIndex));
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

    void install(final AppInfoController appInfoController) {

        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Downloading " + appInfoController.getAppBasicInfoController().getTitle() + " ...")
                .progress(false, 100, true)
                .show();
        subscription = appInfoController.getInstallInfoController().install(getActivity())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
//                        applicationManager.startMCerebrumService(appInfoController);
//                        applicationManager.startMCerebrumService();

                        //                       new AppInstall(appInfo).install(getActivity());
//                        Intent returnIntent = new Intent();
//                        returnIntent.putExtra("type", TYPE_GENERAL);
//                        setResult(Activity.RESULT_OK, returnIntent);
//                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(getActivity(), "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc", "total=" + downloadInfo.getTotalFileSize() + "downloaded=" + downloadInfo.getCurrentFileSize() + " progressWithBar=" + downloadInfo.getProgress());
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(br);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
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
                    dataManager.getApplicationManager().reset(packageName);
                    isUpdateUI = true;
//                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppInstall()).commitAllowingStateLoss();
                    break;
            }
        }
    };

}
