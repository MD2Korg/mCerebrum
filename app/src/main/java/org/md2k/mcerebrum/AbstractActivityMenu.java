package org.md2k.mcerebrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.md2k.mcerebrum.UI.app_install_uninstall.FragmentFoldingUIAppInstall;
import org.md2k.mcerebrum.UI.app_settings.FragmentFoldingUIAppSettings;
import org.md2k.mcerebrum.UI.home.FragmentHome;
import org.md2k.mcerebrum.UI.joinstudy.FragmentJoinStudy;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.login.FragmentLogin;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.menu.ResponseCallBack;

import es.dmoral.toasty.Toasty;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        updateUI();
    }

    @Override
    public void updateUI() {
            createDrawer();
            result.resetDrawerContent();
            result.getHeader().refreshDrawableState();
            result.setSelection(AbstractMenu.MENU_HOME);
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
                case AbstractMenu.MENU_HOME:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentHome()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_APP_ADD_REMOVE:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppInstall()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_APP_SETTINGS:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentFoldingUIAppSettings()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_JOIN:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentJoinStudy()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_LEAVE:
                    materialDialog= Dialog.simple(AbstractActivityMenu.this, "Leave Study", "Do you want to leave the study?", "Yes", "Cancel", new DialogCallback() {
                        @Override
                        public void onSelected(String value) {
                            if(value.equals("Yes")){
                                configManager.clear();
                                prepareConfig();
                            }
                        }
                    }).show();
                    break;
                case AbstractMenu.MENU_LOGIN:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentLogin()).commitAllowingStateLoss();
//                Intent i = new Intent(this, ActivityLogin.class);
//                startActivityForResult(i, ID_JOIN_STUDY);
                    break;
                case AbstractMenu.MENU_LOGOUT:
//                ((UserServer) user).setLoggedIn(this,false);
                    userInfo.setLoggedIn(false);
                    Toasty.success(AbstractActivityMenu.this, "Logged out", Toast.LENGTH_SHORT, true).show();
                    updateUI();
                    break;
                default:
            }
        }
    };

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        super.onDestroy();
    }
}

