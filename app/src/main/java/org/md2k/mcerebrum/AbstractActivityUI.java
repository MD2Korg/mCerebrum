package org.md2k.mcerebrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.md2k.mcerebrum.menu.Menu;
import org.md2k.mcerebrum.menu.ResponseCallBack;

public abstract class AbstractActivityUI extends AbstractActivityBasics {
    private AccountHeader headerResult = null;
    private static final int REQUEST_CODE=101;
    private Drawer result = null;
//    private int prevState=0, curState=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    void updateUI() {
        switch (currentState) {
            case STATE_CONFIGURE_STUDY:
                Intent intent = new Intent(this, ActivityConfigureStudy.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    void refresh() {
        createDrawer();
        result.setSelection(0, false);
    }

    void createDrawer() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
//                .withHeaderBackground(user.getBackground(this))
                .withCompactStyle(true)
                .addProfiles(Menu.getHeaderContent(this, responseCallBack))
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(Menu.getMenuContent(this, responseCallBack))
                .build();
    }

    public void refresh(int state) {
        createDrawer();
        result.resetDrawerContent();
        result.getHeader().refreshDrawableState();
//        prevState = curState;
//        curState = state;
//        result.setSelection(curState, false);
//        changeState(prevState, curState);
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

    abstract void changeState(int prevState, int curState);

    ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(int response) {
//            prevState = curState;
//            curState = response;
//            changeState(prevState, curState);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

}

