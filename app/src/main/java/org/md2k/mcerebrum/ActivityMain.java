package org.md2k.mcerebrum;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
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

import org.md2k.mcerebrum.feedback.FragmentFeedBack;
import org.md2k.mcerebrum.intro.ActivityIntro;
import org.md2k.mcerebrum.login.ActivityLogin;

public class ActivityMain extends AppCompatActivity {
    private static final int PROFILE_SETTING = 1;
    private AccountHeader headerResult = null;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        getSupportActionBar().setHomeActionContentDescription("Application List");
        final IProfile profile = new ProfileDrawerItem().withName("Nusrat Nasrin").withIcon(R.drawable.mcerebrum);
// Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_settings).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                // do something with the clicked item :
                                //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                                //  toast.show();
                                Intent i = new Intent(ActivityMain.this, ActivityLogin.class);
                                startActivity(i);

                                return false;
                            }
                        }),
                        new ProfileSettingDrawerItem().withName("Logout").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_phone_locked).actionBar().paddingDp(5).colorRes(R.color.material_drawer_dark_primary_text)).withIdentifier(PROFILE_SETTING)
                )
                .withSavedInstance(savedInstanceState)
                .build();
//Create the drawer
//Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_cog),
                        new PrimaryDrawerItem().withName("Report").withIcon(FontAwesome.Icon.faw_bar_chart),
                        new PrimaryDrawerItem().withName("Plot").withIcon(FontAwesome.Icon.faw_line_chart),
                        new PrimaryDrawerItem().withName("Export Data").withIcon(FontAwesome.Icon.faw_upload),
                        new SectionDrawerItem().withName("Help"),
                        new SecondaryDrawerItem().withName("Quick Tour").withIcon(FontAwesome.Icon.faw_film),
                        new SecondaryDrawerItem().withName("Frequently ask question").withIcon(FontAwesome.Icon.faw_question_circle),
                        new SectionDrawerItem().withName("About"),
                        new SecondaryDrawerItem().withName("Who we are").withIcon(FontAwesome.Icon.faw_users),
                        new SecondaryDrawerItem().withName("Terms and Conditions").withIcon(FontAwesome.Icon.faw_pencil_square_o),
                        new SecondaryDrawerItem().withName("Privacy Policy").withIcon(FontAwesome.Icon.faw_lock),
                        new SecondaryDrawerItem().withName("Contact").withIcon(FontAwesome.Icon.faw_envelope_o),
                        new SecondaryDrawerItem().withName("Feedback").withIcon(FontAwesome.Icon.faw_comment)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                            startSupportActionMode(new ActionBarCallBack());
                            findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(ActivityMain.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                        }
                        if (drawerItem instanceof Nameable) {
                            toolbar.setTitle(((Nameable) drawerItem).getName().getText(ActivityMain.this));
                            loadFragment(drawerItem);
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        // set the selection to the item with the identifier 5
        if (savedInstanceState == null) {
            result.setSelection(5, false);
        }
        showIntro();
    }
    void loadFragment(IDrawerItem drawerItem){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (((Nameable)drawerItem).getName().getText()){
            case "Feedback":
                ft.replace(R.id.fragment_container, new FragmentFeedBack());
                break;
            case "Contact":
                ft.replace(R.id.fragment_container, new FragmentFeedBack());
                break;
        }
// Complete the changes added above
        ft.commit();
    }

    private class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(UIUtils.getThemeColorFromAttrOrRes(ActivityMain.this, R.attr.colorPrimaryDark, R.color.material_drawer_primary_dark));
            }

            mode.getMenuInflater().inflate(R.menu.cab, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
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
    void showIntro(){
        Intent intent=new Intent(this, ActivityIntro.class);
        startActivityForResult(intent, 10);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("abc","abc");
        Intent intent=new Intent(this, ActivityLogin.class);
        startActivity(intent);
    }
}

