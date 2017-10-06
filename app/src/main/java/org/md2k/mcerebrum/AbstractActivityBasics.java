package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.Utils;

import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.configuration.DataFileManager;
import org.md2k.mcerebrum.data.DataManager;
import org.md2k.system.app.AppInfoController;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.provider.DataCPManager;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import rx.Subscription;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG=AbstractActivityBasics.class.getSimpleName();
    public DataManager dataManager;
//    public UserInfoController userInfoController;
//    public StudyInfoController studyInfoController;

/*    public DataFileManager configManager;
*/
    Subscription subscription;
    MaterialDialog materialDialog;
    Toolbar toolbar;

    abstract void updateUI();
    void resetConfig(){
        if(dataManager!=null && dataManager.getApplicationManager()!=null) dataManager.getApplicationManager().stopMCerebrumService();
        DataFileManager dfm=new DataFileManager();
        DataCPManager dcpm = new DataCPManager(this);
        dataManager = new DataManager(dfm, dcpm);
        if(dataManager.getDataCPManager().getStudyCP().getStarted()) {
            startStudy();
        }else{
            dataManager.getApplicationManager().startMCerebrumService();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    finish();
                }else{
                    resetConfig();
                    updateUI();
//                    prepareConfig();
                }
            }
        });
    }

/*
    public boolean readConfig(){
        if(configManager.read() && configManager.isConfigured()) {
                userInfoController.set();
                studyInfoController.set(configManager.getDataFile());
                applicationManager.set(configManager.getDataFile().getApplications());
                if (studyInfoController.getType().equalsIgnoreCase(STUDY.FREEBIE))
                    userInfoController.setTitle("Default");
                return true;
        }else{
            configManager.clear();
            userInfoController.clear();
            studyInfoController.clear();
            applicationManager.stop();
            return false;
        }
    }
*/
/*
    public void prepareConfig(){
        if(!readConfig()){
            downloadConfigDefault();
        }else
            updateUI();
    }
*/
/*
    void downloadConfigDefault(){
        materialDialog = Dialog.progressWithBar(this, "Loading configuration file...").show();
        subscription = configManager.downloadAndExtractDefault(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onCompleted() {
                        materialDialog.dismiss();
                        readConfig();
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
*/
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
        dataManager.getApplicationManager().stopMCerebrumService();
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void startStudy(){
        ArrayList<AppInfoController> a = dataManager.getApplicationManager().getByType(MCEREBRUM.APP.TYPE_STUDY);
        if(!dataManager.getApplicationManager().isCoreInstalled() || a.size()==0) {
            Toasty.error(this, "Study/DataKit not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        dataManager.getDataCPManager().getStudyCP().setStarted(this, true);
        Intent intent = getPackageManager().getLaunchIntentForPackage(a.get(0).getAppBasicInfoController().getPackageName());
        startActivity(intent);
        dataManager.getApplicationManager().stopMCerebrumService();
        finish();
    }
}

