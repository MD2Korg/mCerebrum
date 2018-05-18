/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.datakit;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.BuildConfig;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.commons.permission.ResultCallback;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyType;
import org.md2k.mcerebrum.datakit.cerebralcortex.CerebralCortexController;
import org.md2k.mcerebrum.datakit.cerebralcortex.ServiceCerebralCortex;
import org.md2k.mcerebrum.datakit.privacy.PrivacyManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Main activity of the application.
 */
public class ActivityMain extends AppCompatActivity {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = ActivityMain.class.getSimpleName();

    /** Privacy management object. */
    PrivacyManager privacyManager;

    /** Whether permissions are granted or not. */
    private boolean isPermission = false;

    /** Cerebral Cortex Controller */
    CerebralCortexController cerebralCortexController;

    /** Receiver for updates from Cerebral Cortex. */
    CerebralCortexUpdateReceiver ccRcvr;

    /**
     * Upon creation, this activity creates a new <code>PermissionInfo</code> object to fetch permissions.
     *
     * <p>
     *     The creation of this activity is logged.
     * </p>
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long curTime = System.currentTimeMillis();
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG).build()).build(), new Crashlytics());
        setContentView(R.layout.activity_datakit_main);

        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            /**
             * Checks the callback result for permissions.
             *
             * @param result Whether permissions are granted or not.
             */
            @Override
            public void onResult(Boolean result) {
                isPermission = result;
                if (result) {
                    if(getIntent().getBooleanExtra("PERMISSION", false))
                        finish();
                    else
                        load();
                }
                else
                    finish();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * When loading the application, logcat offline storage is enabled for warnings and errors and
     * the following method calls are made: <code>configureAppStatus()</code>, <code>setupPrivacyUI</code>,
     * and <code>setupCerebralCortexUI</code>.
     */
    void load() {
        try {
            configureAppStatus();
            setupPrivacyUI();
            setupCerebralCortexUI();
            ccRcvr = new CerebralCortexUpdateReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the application's visibility to invisible.
     */
    void configureAppStatus() {
        findViewById(R.id.textViewTap).setVisibility(View.GONE);
    }

    /**
     * Message handler.
     */
    Handler mHandler = new Handler();


    /**
     * Runnable
     */
    Runnable runnable = new Runnable() {
        /**
         * Computes runtime of the application.
         */
        @Override
        public void run() {
            {
                long time = serviceRunningTime(getApplicationContext(), Constants.SERVICE_NAME);
                if (time < 0) {
                    ((Button) findViewById(R.id.button_app_status)).setText(R.string.inactive);
                    findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this,
                                    R.drawable.button_status_off));
                } else {
                    long runtime = time / 1000;
                    int second = (int) (runtime % 60);
                    runtime /= 60;
                    int minute = (int) (runtime % 60);
                    runtime /= 60;
                    int hour = (int) runtime;
                    ((Button) findViewById(R.id.button_app_status)).setText(String.format("%02d:%02d:%02d",
                            hour, minute, second));
                }
                updateUI();
                updateCCUI();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * Makes the user interface for Cerebral Cortex visible if <code>cerebralCortextController</code>
     * is not available.
     *
     * @throws IOException
     */
    void setupCerebralCortexUI() throws IOException {
        cerebralCortexController = CerebralCortexController.getInstance(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_cerebralcortex);

        if (!cerebralCortexController.isAvailable()) {
            linearLayout.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            updateCCUI();

            Button button = (Button) findViewById(R.id.button_cerebralcortex);
            button.setOnClickListener(new View.OnClickListener() {
                /**
                 * Toggles Cerebral Cortex when clicked.
                 *
                 * @param v Android view
                 */
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityMain.this, ServiceCerebralCortex.class);
                    if (!cerebralCortexController.isActive()) {
                        startService(intent);
                    } else {
                        stopService(intent);
                    }
                }
            });
        }
    }

    /**
     * Sets up the user interface for privacy settings.
     *
     * @throws IOException
     */
    void setupPrivacyUI() throws IOException {
        privacyManager = PrivacyManager.getInstance(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_privacy);
        linearLayout.setVisibility(View.VISIBLE);
        updateUI();

        Button button = (Button) findViewById(R.id.button_privacy);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts <code>ActivityPrivacy</code> when clicked.
             *
             * @param v Android view.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityPrivacy.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Updates the on/off indicator for Cerebral Cortex.
     */
    public void updateCCUI() {
        TextView textViewStatus = ((TextView) findViewById(R.id.textViewcerebralcortexStatus));

        if (cerebralCortexController.isActive()) {
            textViewStatus.setText("ON (Running)");
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
        } else {
            textViewStatus.setText("OFF");
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.red_700));
        }
    }

    /**
     * Updates the user interface with information about <code>PrivacyManager</code>.
     */
    public void updateUI() {
        TextView textViewStatus = ((TextView) findViewById(R.id.textViewPrivacyStatus));
        TextView textViewOption = ((TextView) findViewById(R.id.textViewPrivacyOption));

        if (privacyManager.isActive()) {
            textViewStatus.setText("ON (" + DateTime.convertTimestampToTimeStr(privacyManager.getRemainingTime()) + ")");
            textViewOption.setText(getPrivacyList(privacyManager.getPrivacyData().getPrivacyTypes()));
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.red_700));
            textViewOption.setTextColor(ContextCompat.getColor(this, R.color.red_200));

        } else {
            textViewStatus.setText("OFF");
            textViewOption.setText("");
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
            textViewOption.setVisibility(View.GONE);
            findViewById(R.id.textViewPrivacyOptionTitle).setVisibility(View.GONE);
        }
    }

    /**
     * Returns the list of privacy types in a string.
     *
     * @param privacyTypeArrayList ArrayList of privacy types.
     * @return The list of privacy types in a string.
     */
    private String getPrivacyList(ArrayList<PrivacyType> privacyTypeArrayList) {
        String list = "";
        for (int i = 0; i < privacyTypeArrayList.size(); i++) {
            if (!list.equals("")) list = ", ";
            list += privacyTypeArrayList.get(i).getTitle();
        }
        return list;
    }

    /**
     * When the application is paused, all runnable messages in the handler are removed and
     * the Cerebral Cortex receiver is unregistered.
     */
    @Override
    public void onPause() {
        if(isPermission) {
            mHandler.removeCallbacks(runnable);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(ccRcvr);
        }
        super.onPause();
    }

    /**
     * When the application is resumed, runnable messages are posted to the handler and the Cerebral
     * Cortex receiver is registered.
     */
    @Override
    public void onResume() {
        if(isPermission) {
            mHandler.post(runnable);
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(ccRcvr, new IntentFilter(org.md2k.mcerebrum.datakit.cerebralcortex.Constants
                            .CEREBRAL_CORTEX_STATUS));
        }
        super.onResume();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu Options menu
     * @return Always returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Constructs an options panel.
     *
     * @param view Android view.
     * @param menu Options menu.
     * @return Whether the preparation was successful.
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {}
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * Provides actions for menu items.
     *
     * @param item Menu item that was selected.
     * @return Whether the action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_settings:
                intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                break;

/*
            case R.id.action_about:
  */
/*              intent = new Intent(this, ActivityAbout.class);
                try {
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_CODE, String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_NAME, this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
  *//*
              break;
            case R.id.action_copyright:
//                intent = new Intent(this, ActivityCopyright.class);
//                startActivity(intent);
                break;
*/
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Receiver for Cerebral Cortex updates.
     */
    private class CerebralCortexUpdateReceiver extends BroadcastReceiver {

        /**
         * Displays the Cerebral Cortex upload message.
         *
         * @param context Android context
         * @param intent Android intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textViewMessage = ((TextView) findViewById(R.id.textViewcerebralcortexMessage));
            textViewMessage.setText(intent.getStringExtra("CC_Upload"));
        }
    }
    public static long serviceRunningTime(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return android.os.SystemClock.elapsedRealtime()-service.activeSince;
            }
        }
        return -1;
    }

}
