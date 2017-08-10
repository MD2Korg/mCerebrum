package org.md2k.mcerebrum.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.data.Data;
import org.md2k.mcerebrum.internet.download.DownloadFile;
import org.md2k.mcerebrum.internet.download.DownloadInfo;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivityLogin extends AppCompatActivity {
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


    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mVisible = true;
        hide();


        final Button button_cancel = (Button) findViewById(R.id.button_login_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });


        MaterialEditText mEdit = null;
        final String[] uname = {null};
        final Button button_login = (Button) findViewById(R.id.button_login);
        final MaterialEditText finalMEdit = mEdit;
        final TextView t=(TextView) findViewById(R.id.textview_logininfo);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText materialEditText = (MaterialEditText) findViewById(R.id.edittext_username);
                MaterialEditText materialEditText1 = (MaterialEditText) findViewById(R.id.edittext_password);
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
        final TextView t=(TextView) findViewById(R.id.textview_logininfo);
        t.setText("Success: Downloading configuration file...");
        t.setTextColor(Color.GREEN);
        DownloadFile downloadFile=new DownloadFile();

        subscription = downloadFile.download("https://github.com/MD2Korg/mCerebrum-Configuration/releases/download/1.4/R724749.zip", this.getExternalFilesDir(null)+"/temp","config.zip")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
            @Override
            public void onCompleted() {
                t.setTextColor(Color.GREEN);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("config_file",ActivityLogin.this.getExternalFilesDir(null)+"/temp/config.zip");

                setResult(Activity.RESULT_OK,returnIntent);
                Data data=new Data();
                data.setUserType(ActivityLogin.this,Data.TYPE_LOGIN);
                data.setUserId(ActivityLogin.this,((MaterialEditText) findViewById(R.id.edittext_username)).getText().toString());
                data.setUserPassword(ActivityLogin.this,((MaterialEditText) findViewById(R.id.edittext_password)).getText().toString());
                data.setServer(ActivityLogin.this,((MaterialEditText) findViewById(R.id.edittext_login_server)).getText().toString());
                data.setLoggedIn(ActivityLogin.this,true);
                data.setRefresh(ActivityLogin.this, true);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                t.setTextColor(Color.RED);
                t.setText("Error: download configuration file failed...please try again");
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
       // delayedHide(100);
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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
