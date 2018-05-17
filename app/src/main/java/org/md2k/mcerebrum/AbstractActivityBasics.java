package org.md2k.mcerebrum;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.Utils;

import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.core.access.SampleProvider;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppInfoColumns;
import org.md2k.mcerebrum.core.access.studyinfo.StudyCP;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.system.appinfo.AppCPObserver;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG = AbstractActivityBasics.class.getSimpleName();
    Subscription subscription;
    MaterialDialog materialDialog;
    Toolbar toolbar;
    AppCPObserver appCPObserver;

    abstract void updateUI();
    abstract void createUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        if (Permission.hasPermission(this)) {
            resetConfig();
            appCPObserver = new AppCPObserver(AbstractActivityBasics.this, new Handler());
            getContentResolver().
                    registerContentObserver(
                            Uri.parse(SampleProvider.CONTENT_URI_BASE + "/" + AppInfoColumns.TABLE_NAME),
                            true,
                            appCPObserver);
            initStart();
            createUI();

        } else {

            Permission.requestPermission(this, new PermissionCallback() {
                @Override
                public void OnResponse(boolean isGranted) {
                    if (!isGranted) {
                        Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                        finish();
                    } else {
                        resetConfig();
                        appCPObserver = new AppCPObserver(AbstractActivityBasics.this, new Handler());
                        getContentResolver().
                                registerContentObserver(
                                        Uri.parse(SampleProvider.CONTENT_URI_BASE + "/" + AppInfoColumns.TABLE_NAME),
                                        true,
                                        appCPObserver);
                        initStart();
                        createUI();
//                    prepareConfig();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        try {
            getContentResolver().unregisterContentObserver(appCPObserver);
        } catch (Exception ignored) {
        }

        if (materialDialog != null)
            materialDialog.dismiss();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
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

    void resetConfig() {
        ConfigManager.load(getApplicationContext(), ConfigManager.LOAD_TYPE.READ);
        if (StudyCP.getStarted(MyApplication.getContext())) {
            ArrayList<String> packageNames = AppBasicInfo.getStudy(getApplicationContext());
            if (packageNames.size() == 0 || !AppInstall.isCoreInstalled(getApplicationContext())) {
                Toasty.error(getApplicationContext(), "Datakit/study is not installed", Toast.LENGTH_SHORT).show();
                StudyCP.setStarted(MyApplication.getContext(), false);
            } else {
                StudyCP.setStarted(getApplicationContext(), true);
                AppAccess.launch(getApplicationContext(), packageNames.get(0));
                finish();
            }
        }

    }
    public void initStart(){
        Log.d("abc","initStart...");
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
        Observable.timer(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread()).map(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                BroadCastMessage.send(AbstractActivityBasics.this);
                return true;
            }
        }).subscribe();
    }
}

