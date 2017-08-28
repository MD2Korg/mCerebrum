package org.md2k.mcerebrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import org.md2k.mcerebrum.login.FragmentLogin;
import org.md2k.mcerebrum.menu.Menu;

import es.dmoral.toasty.Toasty;

public class ActivityMain extends AbstractActivityUI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void changeState(int prevState, int curState) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (curState) {
            case Menu.OP_ABOUT_STUDY:
                break;
            case Menu.OP_HELP:
                break;
            case Menu.OP_HOME:
                break;
            case Menu.OP_JOIN:
                break;
            case Menu.OP_LEAVE:
                break;
            case Menu.OP_LOGIN:
                ft.replace(R.id.fragment_container, new FragmentLogin());
//                Intent i = new Intent(this, ActivityLogin.class);
//                startActivityForResult(i, ID_JOIN_STUDY);
                break;
            case Menu.OP_LOGOUT:
//                ((UserServer) user).setLoggedIn(this,false);
                Toasty.success(this, "Success: Logged out", Toast.LENGTH_SHORT, true).show();
                refresh(Menu.OP_HOME);
                break;
            case Menu.OP_SETTINGS:
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            Log.d("abc","abc");
            super.onActivityResult(requestCode, resultCode, intent);
            if(requestCode==INTRO_ID) {
                Intent newIntent = new Intent(this, ActivityUsageType.class);
                startActivityForResult(newIntent, ID_JOIN_STUDY);
            }else if(requestCode==ID_JOIN_STUDY){
                switch(intent.getIntExtra("type",-1)){
                    case ActivityUsageType.TYPE_LOGIN:
                    case ActivityUsageType.TYPE_DOWNLOAD:
                    case ActivityUsageType.TYPE_BARCODE:
                        boolean a=false;
                        String fileName=intent.getStringExtra("config_file");
    //                    boolean b = unzipFile(fileName, folder.getAbsolutePath());
                        boolean b=unzipFile(fileName, this.getExternalFilesDir(null).toString()+"/temp");
                        Intent intentSettings=new Intent(this,ActivityConfig.class);
                        startActivity(intentSettings);
                        Log.d("abc","abc");

                }
                Data data=new Data();
                data.setFirstTimeRunning(this);
            }
        }
        IProfile getProfileJoin() {
            return new ProfileSettingDrawerItem().withName(STRING_JOIN_STUDY).withIcon(FontAwesome.Icon.faw_link).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // do something with the clicked item :
                    //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                    //  toast.show();
                    Intent i = new Intent(AbstractActivityUI.this, ActivityUsageType.class);
                    startActivityForResult(i, ID_JOIN_STUDY);
                    return false;
                }
            });
        }

        IProfile getProfileLeave() {
            return new ProfileSettingDrawerItem().withName("Leave Study").withIcon(FontAwesome.Icon.faw_chain_broken).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // do something with the clicked item :
                    //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                    //  toast.show();
                    Data data = new Data();
                    data.setUserType(AbstractActivityUI.this, Data.TYPE_DEFAULT);
                    data.setLoggedIn(AbstractActivityUI.this, false);
                    data.setRefresh(AbstractActivityUI.this, true);
                    Toasty.success(AbstractActivityUI.this, "Success: Left from the study", Toast.LENGTH_SHORT, true).show();
                    refresh();
                    return false;
                }
            });
        }

        IProfile getProfileAboutStudy() {
            return new ProfileSettingDrawerItem().withName("About Study").withIcon(FontAwesome.Icon.faw_info).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // do something with the clicked item :
                    //    Toast toast= Toast.makeText(MainActivity.this, "login enter", Toast.LENGTH_SHORT);
                    //  toast.show();
                    Data data = new Data();
                    data.setUserType(AbstractActivityUI.this, Data.TYPE_DEFAULT);
                    data.setLoggedIn(AbstractActivityUI.this, false);
                    data.setRefresh(AbstractActivityUI.this, true);
                    refresh();
                    return false;
                }
            });
        }

        void refresh() {
            Data data = new Data();
            if (data.isRefresh(this)) {
                createDrawer();

                result.resetDrawerContent();

                result.getHeader().refreshDrawableState();
                data.setRefresh(this, false);
            }
        }
        void getProfilesLogin() {
            IProfile profile1, profile2, profile3, profile4;
            final Data data = new Data();
            profile1 = new ProfileDrawerItem().withName(data.getUserId(this)).withIcon(R.drawable.mcerebrum);
            profile2 = getProfileAboutStudy();
            if (!data.isLoggedIn(this)) {
                profile3 = new ProfileSettingDrawerItem().withName("Login").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toasty.success(AbstractActivityUI.this, "Success: Logged in", Toast.LENGTH_SHORT, true).show();
                        data.setLoggedIn(AbstractActivityUI.this, true);
                        data.setRefresh(AbstractActivityUI.this, true);
                        refresh();
                        return false;
                    }
                });
            }
            profile4 = getProfileLeave();

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

        void loadFragment(IDrawerItem drawerItem) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (((Nameable) drawerItem).getName().getText()) {
                case STRING_HOME:
                    lastId = curId;
                    curId = ID_HOME;
                    break;
                case STRING_SETTINGS:
                    lastId = curId;
                    curId = ID_SETTINGS;
                    Intent intentSettings = new Intent(this, ActivityConfig.class);
                    startActivity(intentSettings);
                    break;
                case STRING_REPORT:
                    lastId = curId;
                    curId = ID_REPORT;
                    break;
                case STRING_PLOT:
                    lastId = curId;
                    curId = ID_PLOT;
                    break;
                case STRING_EXPORT_DATA:
                    lastId = curId;
                    curId = ID_EXPORT_DATA;
                    break;
                case STRING_HELP:
                    lastId = curId;
                    curId = ID_HELP;
                    break;
                case STRING_QUICK_TOUR:
                    lastId = curId;
                    curId = ID_QUICK_TOUR;
                    Intent intent = new Intent(this, ActivityIntro.class);
                    startActivityForResult(intent, ID_QUICK_TOUR);
                    break;
                case STRING_FAQ:
                    lastId = curId;
                    curId = ID_FAQ;
                    break;
                case STRING_ABOUT:
                    lastId = curId;
                    curId = ID_ABOUT;
                    break;
                case STRING_WHO_WE_ARE:
                    lastId = curId;
                    curId = ID_WHO_WE_ARE;
                    ft.replace(R.id.fragment_container, new FragmentWhoWeAre());
                    break;
                case STRING_TERMS:
                    lastId = curId;
                    curId = ID_TERMS;
                    break;
                case STRING_PRIVACY:
                    lastId = curId;
                    curId = ID_PRIVACY;
                    break;
                case STRING_CONTACT:
                    lastId = curId;
                    curId = ID_CONTACT;
                    break;
                case STRING_FEEDBACK:
                    lastId = curId;
                    curId = ID_FEEDBACK;
                    ft.replace(R.id.fragment_container, new FragmentFeedBack());
                    break;
            }
    // Complete the changes added above
            ft.commit();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            Log.d("abc", "abc");
            super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == ID_QUICK_TOUR) {
                curId = lastId;
                result.setSelection(curId);
            }
        }

        */
    @Override
    public void onResume() {
        super.onResume();
    }
}

