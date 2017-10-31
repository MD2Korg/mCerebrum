package org.md2k.mcerebrum;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.md2k.mcerebrum.UI.app_install_uninstall.FragmentAppInstall;
import org.md2k.mcerebrum.UI.app_settings.FragmentAppSettings;
import org.md2k.mcerebrum.UI.check_update.ActivityCheckUpdate;
import org.md2k.mcerebrum.UI.home.FragmentHome;
import org.md2k.mcerebrum.UI.joinstudy.FragmentJoinStudy;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.configinfo.ConfigCP;
import org.md2k.mcerebrum.core.access.serverinfo.ServerCP;
import org.md2k.mcerebrum.core.access.studyinfo.StudyCP;
import org.md2k.mcerebrum.core.access.userinfo.UserCP;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.configuration.ConfigManager;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.menu.ResponseCallBack;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.update.Update;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private Drawer result = null;
    public int selectedMenu = AbstractMenu.MENU_HOME;

    @Override
    public void updateUI() {
        if(result==null) {createUI();return;}
        int index = selectedMenu;
        if(index==-1) index = AbstractMenu.MENU_HOME;
        int badgeValue=Update.hasUpdate(AbstractActivityMenu.this);
        if(badgeValue>0){
            StringHolder a = new StringHolder(String.valueOf(badgeValue));
            result.updateBadge(AbstractMenu.MENU_CHECK_UPDATE, a);
        }else{
            StringHolder a = new StringHolder("");
            result.updateBadge(AbstractMenu.MENU_CHECK_UPDATE, a);
        }
/*
        if(menuContent[i].badgeValue>0){
            ((PrimaryDrawerItem)(iDrawerItems[i])).withBadge(String.valueOf(menuContent[i].badgeValue)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));;
        }
*/

/*
        createDrawer();
        result.resetDrawerContent();
        result.getHeader().refreshDrawableState();
*/
//        result.setSelection(index);
    }
    @Override
    public void createUI() {
        createDrawer();
        result.resetDrawerContent();
        result.getHeader().refreshDrawableState();
        result.setSelection(AbstractMenu.MENU_HOME);
    }

    void createDrawer() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(AbstractMenu.getCoverImage(this, StudyCP.getCoverImage(MyApplication.getContext())))
                .withCompactStyle(true)
                .addProfiles(AbstractMenu.getHeaderContent(this, UserCP.getUserName(MyApplication.getContext()), responseCallBack))
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(AbstractMenu.getMenuContent(this, responseCallBack))
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
            } else
                super.onBackPressed();
        }
    }


    public ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(IDrawerItem drawerItem, int responseId) {
            selectedMenu = responseId;
            if (drawerItem != null)
                toolbar.setTitle(StudyCP.getTitle(MyApplication.getContext()) + ": " + ((Nameable) drawerItem).getName().getText(AbstractActivityMenu.this));
            else toolbar.setTitle(StudyCP.getTitle(MyApplication.getContext()));
            switch (responseId) {
                case AbstractMenu.MENU_HOME:
                    toolbar.setTitle(StudyCP.getTitle(MyApplication.getContext()));
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
                    Intent intent =new Intent(AbstractActivityMenu.this, ActivityCheckUpdate.class);
                    startActivity(intent);
                    createUI();
                    break;

                case AbstractMenu.MENU_LEAVE:
                    materialDialog = Dialog.simple(AbstractActivityMenu.this, "Leave Study", "Do you want to leave the study?", "Yes", "Cancel", new DialogCallback() {
                        @Override
                        public void onSelected(String value) {
                            if (value.equals("Yes")) {
                                ServerCP.deleteTable(getApplicationContext());
                                ConfigManager.loadFromAsset(getApplicationContext());
                                ConfigCP.setDownloadFrom(getApplicationContext(), MCEREBRUM.CONFIG.TYPE_FREEBIE);
                                createUI();
                            }
                        }
                    }).show();
                    break;
                case AbstractMenu.MENU_STUDY_START:
                    ArrayList<String> packageNames = AppBasicInfo.getStudy(getApplicationContext());
                    if (packageNames.size() == 0 || !AppInstall.isCoreInstalled(getApplicationContext())) {
                        Toasty.error(getApplicationContext(), "Datakit/study is not installed", Toast.LENGTH_SHORT).show();
                    } else {
                        StudyCP.setStarted(getApplicationContext(), true);
                        AppAccess.launch(getApplicationContext(), packageNames.get(0));
                        finish();
                    }
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

