package org.md2k.mcerebrum.UI.app_install_uninstall;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.mcerebrum.MainApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.system.appinfo.AppInstall;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityAppInstall extends AppCompatActivity {
    MaterialDialog materialDialog;
    Subscription subscription;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_install);
        packageName=getIntent().getStringExtra("package_name");
        install();
    }
    void install(){
        materialDialog = new MaterialDialog.Builder(this)
                .content("Downloading " + AppBasicInfo.getTitle(MainApplication.getContext(), packageName) + " ...")
                .progress(false, 100, true)
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(subscription!=null && !subscription.isUnsubscribed())
                            subscription.unsubscribe();
                        if(materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
                        finish();

                    }
                })
                .show();
        subscription = AppInstall.install(this, packageName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityAppInstall.this, "Error: Download failed (e=" + e.toString() + ")").show();
                        materialDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        Log.d("abc", "total=" + downloadInfo.getTotalFileSize() + "downloaded=" + downloadInfo.getCurrentFileSize() + " progressWithBar=" + downloadInfo.getProgress());
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });

    }
}
