package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.md2k.mcerebrum.UI.app_install_uninstall.FragmentAppInstall;
import org.md2k.mcerebrum.UI.app_settings.FragmentAppSettings;
import org.md2k.mcerebrum.UI.check_update.FragmentCheckUpdate;
import org.md2k.mcerebrum.UI.home.FragmentHome;
import org.md2k.mcerebrum.UI.joinstudy.FragmentJoinStudy;
import org.md2k.system.app.ApplicationManager;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.menu.ResponseCallBack;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private Drawer result = null;
    int selectedMenu = AbstractMenu.MENU_HOME;

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
                .withHeaderBackground(AbstractMenu.getCoverImage(this, dataManager.getDataCPManager().getStudyCP().getCoverImage()))
                .withCompactStyle(true)
                .addProfiles(AbstractMenu.getHeaderContent(this, dataManager.getDataCPManager().getUserCP().getUserName(), dataManager.getDataCPManager().getStudyCP(), dataManager.getDataCPManager().getConfigCP(), responseCallBack))
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(AbstractMenu.getMenuContent(this, dataManager.getDataCPManager().getStudyCP(), dataManager.getDataCPManager().getConfigCP(), responseCallBack))
                .build();
    }


    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (selectedMenu != AbstractMenu.MENU_HOME) {
                responseCallBack.onResponse(null, AbstractMenu.MENU_HOME);
            }else
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

    public ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(IDrawerItem drawerItem, int responseId) {
            selectedMenu = responseId;
            if (drawerItem != null)
                toolbar.setTitle(dataManager.getDataCPManager().getStudyCP().getTitle() + ": " + ((Nameable) drawerItem).getName().getText(AbstractActivityMenu.this));
            else toolbar.setTitle(dataManager.getDataCPManager().getStudyCP().getTitle());
            switch (responseId) {
                case AbstractMenu.MENU_HOME:
                    toolbar.setTitle(dataManager.getDataCPManager().getStudyCP().getTitle());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_APP_ADD_REMOVE:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAppInstall()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_APP_SETTINGS:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentAppSettings()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_JOIN:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentJoinStudy()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_CHECK_UPDATE:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentCheckUpdate()).commitAllowingStateLoss();
                    break;

                case AbstractMenu.MENU_LEAVE:
                    materialDialog = Dialog.simple(AbstractActivityMenu.this, "Leave Study", "Do you want to leave the study?", "Yes", "Cancel", new DialogCallback() {
                        @Override
                        public void onSelected(String value) {
                            if (value.equals("Yes")) {
                                dataManager.getApplicationManager().stopMCerebrumService();
                                dataManager.loadDefault();
                                dataManager.getApplicationManager().startMCerebrumService();
                                updateUI();
//                                prepareConfig();
                            }
                        }
                    }).show();
                    break;
/*
                case AbstractMenu.MENU_LOGIN:
                    Intent intent = new Intent(AbstractActivityMenu.this, ActivityLogin.class);
                    startActivityForResult(intent, REQUESTCODE_LOGIN);
//                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentLogin()).commitAllowingStateLoss();
                    break;
                case AbstractMenu.MENU_LOGOUT:
//                ((UserServer) user).setLoggedIn(this,false);
                    dataManager.getDataCPManager().getUserCP().setLoggedIn(false);
                    Toasty.success(AbstractActivityMenu.this, "Logged out", Toast.LENGTH_SHORT, true).show();
                    updateUI();
                    break;
*/
                case AbstractMenu.MENU_STUDY_START:
                    startStudy();
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

