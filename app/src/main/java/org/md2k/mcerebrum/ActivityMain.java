package org.md2k.mcerebrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.commons.permission.ResultCallback;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.menu.AbstractMenu;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityMain extends AbstractActivityMenu {
    private static final int REQUEST_CODE = 100;
    boolean isFirstTime;
    boolean isPermissionGranted =false;
    Subscription subscription;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirstTime=true;
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (!result) {
                    Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                }else
                    isPermissionGranted =true;
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        if(!isPermissionGranted) return;
        if(!configManager.isConfigured(this)){
            downloadConfig();
//            Intent intent=new Intent(this, ActivityConfigureStudy.class);
//            startActivityForResult(intent, REQUEST_CODE);
        }else{
            updateMenu(AbstractMenu.MENU_HOME);
            isFirstTime=false;
        }

    }
    void downloadConfig() {

        ConfigManager configManager = new ConfigManager();
        materialDialog = new MaterialDialog.Builder(this)
                .content("Downloading configuration file...")
                .progress(false, 100, true)
                .show();
        subscription = configManager.downloadAndExtract(this, Constants.CONFIG_DEFAULT_FILENAME)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        updateMenu(AbstractMenu.MENU_HOME);

//                        Intent returnIntent = new Intent();
//                        returnIntent.putExtra("type", TYPE_GENERAL);
//                        setResult(Activity.RESULT_OK, returnIntent);
//                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(ActivityMain.this, "Error: Download failed (e=" + e.getMessage() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            if(resultCode==RESULT_CANCELED)
                finish();
            else{
                updateMenu(AbstractMenu.MENU_HOME);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        ;
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }

}

