package org.md2k.mcerebrum.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;
import org.md2k.mcerebrum.menu.AbstractMenu;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FragmentLogin extends Fragment {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    TextView textview_loginInfo;


    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    Subscription subscription;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_login, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        textview_loginInfo = (TextView) view.findViewById(R.id.textview_logininfo);
        mVisible = true;
        hide();
        final Button button_cancel = (Button) view.findViewById(R.id.button_login_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ActivityMain)getActivity()).updateMenu(AbstractMenu.MENU_HOME);
            }
        });


        MaterialEditText mEdit = null;
        final String[] uname = {null};
        final Button button_login = (Button) view.findViewById(R.id.button_login);
        final MaterialEditText finalMEdit = mEdit;
        final TextView t=(TextView) view.findViewById(R.id.textview_logininfo);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText materialEditText = (MaterialEditText) view.findViewById(R.id.edittext_username);
                MaterialEditText materialEditText1 = (MaterialEditText) view.findViewById(R.id.edittext_password);
                //   Toast.makeText(ActivityLogin.this, "username=" + materialEditText.getText(), Toast.LENGTH_LONG).show();
                //    Toast.makeText(ActivityLogin.this, "password=" + materialEditText1.getText(), Toast.LENGTH_LONG).show();




                if(materialEditText.getText().toString().equals("")){
                    t.setText("Error: User Name is required");
                    t.setTextColor(Color.RED);

                }
                else if(materialEditText1.getText().toString().equals("")){
                    t.setText("Error: Password is required");
                    t.setTextColor(Color.RED);

                }

                else if(materialEditText.getText().toString().equals("abc")&& materialEditText1.getText().toString().equals("123")){
                    downloadConfig();
                }
                else if(!materialEditText.getText().toString().equals("abc")|| !materialEditText1.getText().toString().equals("123")){
                    t.setText("Error: Invalid username/password");
                    t.setTextColor(Color.RED);
                }
            };


        });
    }

    private void downloadConfig(){
        textview_loginInfo.setText("Success: Downloading configuration file...");
        textview_loginInfo.setTextColor(Color.GREEN);
        DownloadFile downloadFile=new DownloadFile();

        subscription = downloadFile.download("https://github.com/MD2Korg/mCerebrum-Configuration/releases/download/1.4/mperf.zip", getActivity().getExternalFilesDir(null)+"/temp","config.zip")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
            @Override
            public void onCompleted() {
                textview_loginInfo.setTextColor(Color.GREEN);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("config_file",getActivity().getExternalFilesDir(null)+"/temp/config.zip");
//                User.createServer(getActivity(), )
//                ((ActivityMain)getActivity()).refresh(AbstractMenu.MENU_SETTINGS);

//                setResult(Activity.RESULT_OK,returnIntent);
//                Data data=new Data();
/*
                data.setUserType(FragmentLogin.this,Data.TYPE_LOGIN);
                data.setUserId(FragmentLogin.this,((MaterialEditText) findViewById(R.id.edittext_username)).getText().toString());
                data.setUserPassword(FragmentLogin.this,((MaterialEditText) findViewById(R.id.edittext_password)).getText().toString());
                data.setServer(FragmentLogin.this,((MaterialEditText) findViewById(R.id.edittext_login_server)).getText().toString());
//                data.setLoggedIn(ActivityLogin.this,true);
                data.setRefresh(FragmentLogin.this, true);
                finish();
*/
            }

            @Override
            public void onError(Throwable e) {
                textview_loginInfo.setTextColor(Color.RED);
                textview_loginInfo.setText("Error: download configuration file failed...please try again");
            }

            @Override
            public void onNext(DownloadInfo downloadInfo) {

            }
        });
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
 //           actionBar.hide();
 //       }

        mVisible = false;
 }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;


    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    @Override
    public void onDestroy(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }
}
