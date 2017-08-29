package org.md2k.mcerebrum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.md2k.mcerebrum.UI.folding_ui.FragmentFoldingUI;
import org.md2k.mcerebrum.login.ActivityLogin;
import org.md2k.mcerebrum.login.FragmentLogin;
import org.md2k.mcerebrum.menu.AbstractMenu;
import org.md2k.mcerebrum.menu.ResponseCallBack;

import es.dmoral.toasty.Toasty;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private static final int REQUEST_CODE_JOIN=102;
    private Drawer result = null;

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
        createDrawer();
        result.resetDrawerContent();
        result.getHeader().refreshDrawableState();
        result.setSelection(state, true);
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
        super.onSaveInstanceState(outState);
    }

    ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(int response) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (response) {
                case AbstractMenu.MENU_ABOUT_STUDY:
                    break;
                case AbstractMenu.MENU_HELP:
                    break;
                case AbstractMenu.MENU_HOME:
                    ft.replace(R.id.fragment_container, new FragmentFoldingUI());
                    ft.commit();
                    break;
                case AbstractMenu.MENU_JOIN:
                    Intent intent=new Intent(AbstractActivityMenu.this, ActivityLogin.class);
                    startActivityForResult(intent, REQUEST_CODE_JOIN);
                    break;
                case AbstractMenu.MENU_LEAVE:
                    break;
                case AbstractMenu.MENU_LOGIN:
                    ft.replace(R.id.fragment_container, new FragmentLogin());
                    ft.commit();
//                Intent i = new Intent(this, ActivityLogin.class);
//                startActivityForResult(i, ID_JOIN_STUDY);
                    break;
                case AbstractMenu.MENU_LOGOUT:
//                ((UserServer) user).setLoggedIn(this,false);
                    Toasty.success(AbstractActivityMenu.this, "Success: Logged out", Toast.LENGTH_SHORT, true).show();
                    // refresh(AbstractMenu.MENU_HOME);
                    break;
                case AbstractMenu.MENU_SETTINGS:
                default:
            }

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_JOIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                updateMenu(AbstractMenu.MENU_HOME);
            }
            else{
                updateMenu(AbstractMenu.MENU_HOME);
//                updateState();
            }
        }
    }

}

