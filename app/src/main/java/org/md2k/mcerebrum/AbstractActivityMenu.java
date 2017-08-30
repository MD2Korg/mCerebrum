package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.md2k.mcerebrum.UI.app_install_uninstall.FragmentFoldingUIAppInstall;
import org.md2k.mcerebrum.UI.app_settings.FragmentFoldingUIAppSettings;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.data.UserInfo;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.login.ActivityLogin;
import org.md2k.mcerebrum.login.FragmentLogin;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.menu.ResponseCallBack;

import es.dmoral.toasty.Toasty;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private static final int REQUEST_CODE_JOIN=102;
    private Drawer result = null;
    Subscription subscription;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void updateMenu(int state) {
        if(!configManager.isConfigured(this)) {
            downloadConfig();
        }else {
            createDrawer();
            result.resetDrawerContent();
            result.getHeader().refreshDrawableState();
            result.setSelection(state, true);
        }
    }

    void createDrawer() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(studyInfo.getCoverImage(this))
                .withCompactStyle(true)
                .addProfiles(AbstractMenu.getHeaderContent(this, userInfo, studyInfo, responseCallBack))
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(AbstractMenu.getMenuContent(this, studyInfo, responseCallBack))
                .build();
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

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
/*
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
*/
//        super.onSaveInstanceState(outState);
    }

    ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(int response) {
            switch (response) {
                case AbstractMenu.MENU_ABOUT_STUDY:
                    break;
                case AbstractMenu.MENU_HELP:
                    break;
                case AbstractMenu.MENU_APP_ADD_REMOVE:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppInstall()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_JOIN:
                    Intent intent=new Intent(AbstractActivityMenu.this, ActivityLogin.class);
                    startActivityForResult(intent, REQUEST_CODE_JOIN);
                    break;
                case AbstractMenu.MENU_LEAVE:
                    getConfirmationToLeave();
                    break;
                case AbstractMenu.MENU_LOGIN:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentLogin()).commitAllowingStateLoss();
//                Intent i = new Intent(this, ActivityLogin.class);
//                startActivityForResult(i, ID_JOIN_STUDY);
                    break;
                case AbstractMenu.MENU_LOGOUT:
//                ((UserServer) user).setLoggedIn(this,false);
                    UserInfo.setLoggedIn(false);
                    Toasty.success(AbstractActivityMenu.this, "Success: Logged out", Toast.LENGTH_SHORT, true).show();
                    updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
                    break;
                case AbstractMenu.MENU_APP_SETTINGS:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppSettings()).commitAllowingStateLoss();
                default:
            }
        }
    };
    void getConfirmationToLeave(){
        new MaterialDialog.Builder(this)
                .title("Leave Study")
                .content("Do you want to leave the study?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        configManager.clear();
                        updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
                        Toasty.success(AbstractActivityMenu.this, "Successfully left from the study", Toast.LENGTH_SHORT).show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_JOIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
            }
            else{
                updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);
//                updateState();
            }
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
                        updateMenu(AbstractMenu.MENU_APP_ADD_REMOVE);

//                        Intent returnIntent = new Intent();
//                        returnIntent.putExtra("type", TYPE_GENERAL);
//                        setResult(Activity.RESULT_OK, returnIntent);
//                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.error(AbstractActivityMenu.this, "Error: Download failed (e=" + e.getMessage() + ")").show();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNext(DownloadInfo downloadInfo) {
                        materialDialog.setProgress((int) downloadInfo.getProgress());
                    }
                });
    }
    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }

}

