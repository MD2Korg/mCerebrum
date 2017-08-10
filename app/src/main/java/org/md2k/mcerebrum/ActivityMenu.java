package org.md2k.mcerebrum;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.UIUtils;

import org.md2k.mcerebrum.data.Data;
import org.md2k.mcerebrum.feedback.FragmentFeedBack;
import org.md2k.mcerebrum.intro.ActivityIntro;
import org.md2k.mcerebrum.setup_step.ActivityConfig;
import org.md2k.mcerebrum.usage_type.ActivityUsageType;

import es.dmoral.toasty.Toasty;

public abstract class ActivityMenu extends AppCompatActivity {
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private static final String STRING_HOME="Home";
    private static final String STRING_SETTINGS="Settings";
    private static final String STRING_REPORT="Report";
    private static final String STRING_PLOT="Plot";
    private static final String STRING_EXPORT_DATA="Export Data";
    private static final String STRING_HELP="Help";
    private static final String STRING_QUICK_TOUR = "Quick Tour";
    private static final String STRING_FAQ = "Frequently asked question";
    private static final String STRING_ABOUT = "About";
    private static final String STRING_WHO_WE_ARE = "Who we are";
    private static final String STRING_TERMS = "Terms and Conditions";
    private static final String STRING_PRIVACY = "Privacy Policy";
    private static final String STRING_CONTACT = "Contact";
    private static final String STRING_FEEDBACK = "FEEDBACK";

    private static final int ID_HOME=0;
    private static final int ID_SETTINGS=1;
    private static final int ID_REPORT=2;
    private static final int ID_PLOT=3;
    private static final int ID_EXPORT_DATA=4;
    private static final int ID_HELP=5;
    private static final int ID_QUICK_TOUR = 6;
    private static final int ID_FAQ = 7;
    private static final int ID_ABOUT = 8;
    private static final int ID_WHO_WE_ARE = 9;
    private static final int ID_TERMS = 10;
    private static final int ID_PRIVACY = 11;
    private static final int ID_CONTACT = 12;
    private static final int ID_FEEDBACK = 13;
    int curId;
    int lastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curId=0;
        lastId=0;
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

//        getSupportActionBar().setHomeActionContentDescription("Application List");
        createDrawer();


        // set the selection to the item with the identifier 5
        if (savedInstanceState == null) {
            result.setSelection(ID_HOME, false);
        }
    }
    void createDrawer(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        createHeader();
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(STRING_HOME).withIcon(FontAwesome.Icon.faw_home).withIdentifier(ID_HOME),
                        new PrimaryDrawerItem().withName(STRING_SETTINGS).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(ID_SETTINGS),
                        new PrimaryDrawerItem().withName(STRING_REPORT).withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(ID_REPORT),
                        new PrimaryDrawerItem().withName(STRING_PLOT).withIcon(FontAwesome.Icon.faw_line_chart).withIdentifier(ID_PLOT),
                        new PrimaryDrawerItem().withName(STRING_EXPORT_DATA).withIcon(FontAwesome.Icon.faw_upload).withIdentifier(ID_EXPORT_DATA),
                        new SectionDrawerItem().withName(STRING_HELP).withIdentifier(ID_HELP),
                        new SecondaryDrawerItem().withName(STRING_QUICK_TOUR).withIcon(FontAwesome.Icon.faw_film).withIdentifier(ID_QUICK_TOUR),
                        new SecondaryDrawerItem().withName(STRING_FAQ).withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(ID_FAQ),
                        new SectionDrawerItem().withName(STRING_ABOUT).withIdentifier(ID_ABOUT),
                        new SecondaryDrawerItem().withName(STRING_WHO_WE_ARE).withIcon(FontAwesome.Icon.faw_users).withIdentifier(ID_WHO_WE_ARE),
                        new SecondaryDrawerItem().withName(STRING_TERMS).withIcon(FontAwesome.Icon.faw_pencil_square_o).withIdentifier(ID_TERMS),
                        new SecondaryDrawerItem().withName(STRING_PRIVACY).withIcon(FontAwesome.Icon.faw_lock).withIdentifier(ID_PRIVACY),
                        new SecondaryDrawerItem().withName(STRING_CONTACT).withIcon(FontAwesome.Icon.faw_envelope_o).withIdentifier(ID_CONTACT),
                        new SecondaryDrawerItem().withName(STRING_FEEDBACK).withIcon(FontAwesome.Icon.faw_comment).withIdentifier(ID_FEEDBACK)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                         if (drawerItem instanceof Nameable) {
                            toolbar.setTitle(((Nameable) drawerItem).getName().getText(ActivityMenu.this));
                            loadFragment(drawerItem);
                        }
                        return false;
                    }
                })
//                .withSavedInstance(savedInstanceState)
                .build();

    }
    void loadFragment(IDrawerItem drawerItem){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (((Nameable)drawerItem).getName().getText()){
            case STRING_HOME:
                lastId=curId;
                curId=ID_HOME;
                break;
            case STRING_SETTINGS:
                lastId=curId;
                curId=ID_SETTINGS;
                Intent intentSettings=new Intent(this, ActivityConfig.class);
                startActivity(intentSettings);
                break;
            case STRING_REPORT:
                lastId=curId;
                curId=ID_REPORT;
                break;
            case STRING_PLOT:
                lastId=curId;
                curId=ID_PLOT;
                break;
            case STRING_EXPORT_DATA:
                lastId=curId;
                curId=ID_EXPORT_DATA;
                break;
            case STRING_HELP:
                lastId=curId;
                curId=ID_HELP;
                break;
            case STRING_QUICK_TOUR:
                lastId=curId;
                curId=ID_QUICK_TOUR;
                Intent intent=new Intent(this, ActivityIntro.class);
                startActivityForResult(intent,ID_QUICK_TOUR);
                break;
            case STRING_FAQ:
                lastId=curId;
                curId=ID_FAQ;
                break;
            case STRING_ABOUT:
                lastId=curId;
                curId=ID_ABOUT;
                break;
            case STRING_WHO_WE_ARE:
                lastId=curId;
                curId=ID_WHO_WE_ARE;
                break;
            case STRING_TERMS:
                lastId=curId;
                curId=ID_TERMS;
                break;
            case STRING_PRIVACY:
                lastId=curId;
                curId=ID_PRIVACY;
                break;
            case STRING_CONTACT:
                lastId=curId;
                curId=ID_CONTACT;
                break;
            case STRING_FEEDBACK:
                lastId=curId;
                curId=ID_FEEDBACK;
                ft.replace(R.id.fragment_container, new FragmentFeedBack());
                break;
        }
// Complete the changes added above
        ft.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("abc","abc");
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==ID_QUICK_TOUR){
            curId=lastId;
            result.setSelection(curId);
        }
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
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    void createHeader(){
        Data data=new Data();
        if(data.getUserType(this)==null) getProfilesDefault();
        else {
            switch (data.getUserType(this)) {
                case Data.TYPE_DEFAULT:
                    getProfilesDefault();
                    break;
                case Data.TYPE_DOWNLOAD:
                    getProfilesDownload();
                    break;
                case Data.TYPE_LOGIN:
                    getProfilesLogin();
                    break;
                default:
                    getProfilesDefault();
                    break;
            }
        }
    }
    void getProfilesLogin(){
        IProfile profile1, profile2, profile3, profile4;
        final Data data=new Data();
        profile1=new ProfileDrawerItem().withName(data.getUserId(this)).withIcon(R.drawable.mcerebrum);
        profile2=getProfileAboutStudy();
        if(!data.isLoggedIn(this)) {
            profile3 = new ProfileSettingDrawerItem().withName("Login").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // do something with the clicked item :
                    //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                    //  toast.show();
//                    Intent i = new Intent(ActivityMenu.this, ActivityLogin.class);
//                    startActivity(i);
                    Toasty.success(ActivityMenu.this, "Success: Logged in", Toast.LENGTH_SHORT, true).show();
                    data.setLoggedIn(ActivityMenu.this, true);
                    data.setRefresh(ActivityMenu.this, true);
                    refresh();
                    return false;
                }
            });
        }
        else{
            profile3 = new ProfileSettingDrawerItem().withName("Logout").withIcon(FontAwesome.Icon.faw_sign_out).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // do something with the clicked item :
                    //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                    //  toast.show();
//                    Intent i = new Intent(ActivityMenu.this, ActivityLogin.class);
//                    startActivity(i);
                    Toasty.success(ActivityMenu.this, "Success: Logged out", Toast.LENGTH_SHORT, true).show();
                    data.setLoggedIn(ActivityMenu.this, false);
                    data.setRefresh(ActivityMenu.this, true);
                    refresh();
                    return false;
                }
            });
        }
        profile4=getProfileLeave();

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(
                        profile1,
                        profile2,
                        profile3,
                        profile4
                )
                .build();
    }
    void getProfilesDefault(){
        IProfile profile1;
        profile1=new ProfileDrawerItem().withName("Default").withIcon(R.drawable.mcerebrum);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(
                        profile1,
                        getProfileJoin()
                )
                .build();

    }
    void getProfilesDownload(){
        IProfile profile1, profile2, profile3, profile4;
        profile1=new ProfileDrawerItem().withName("p001202").withIcon(R.drawable.mcerebrum);
        profile2=new ProfileDrawerItem().withName("About Study").withIcon(FontAwesome.Icon.faw_info_circle);
        profile3=getProfileLeave();

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(
                        profile1,
                        profile2,
                        profile3
                )
                .build();

    }

    IProfile getProfileJoin(){
        return new ProfileSettingDrawerItem().withName("Join Study").withIcon(FontAwesome.Icon.faw_link).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                // do something with the clicked item :
                //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                //  toast.show();
                Intent i = new Intent(ActivityMenu.this, ActivityUsageType.class);
                startActivity(i);

                return false;
            }
        });
    }
    IProfile getProfileLeave(){
        return new ProfileSettingDrawerItem().withName("Leave Study").withIcon(FontAwesome.Icon.faw_chain_broken).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                // do something with the clicked item :
                //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                //  toast.show();
                Data data=new Data();
                data.setUserType(ActivityMenu.this, Data.TYPE_DEFAULT);
                data.setLoggedIn(ActivityMenu.this, false);
                data.setRefresh(ActivityMenu.this, true);
                Toasty.success(ActivityMenu.this, "Success: Left from the study", Toast.LENGTH_SHORT, true).show();
                refresh();
                return false;
            }
        });
    }
    IProfile getProfileAboutStudy(){
        return new ProfileSettingDrawerItem().withName("About Study").withIcon(FontAwesome.Icon.faw_info).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                // do something with the clicked item :
                //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                //  toast.show();
                Data data=new Data();
                data.setUserType(ActivityMenu.this, Data.TYPE_DEFAULT);
                data.setLoggedIn(ActivityMenu.this, false);
                data.setRefresh(ActivityMenu.this, true);
                refresh();
                return false;
            }
        });
    }
    void refresh(){
        Data data=new Data();
        if(data.isRefresh(this)) {
            createDrawer();

            result.resetDrawerContent();

            result.getHeader().refreshDrawableState();
            data.setRefresh(this, false);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }
}

