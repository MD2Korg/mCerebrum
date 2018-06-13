package org.md2k.mcerebrum;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.md2k.mcerebrum.commons.permission.PermissionInfo;
import org.md2k.mcerebrum.commons.permission.ResultCallback;
import org.md2k.mcerebrum.core.access.MCerebrum;
import org.md2k.mcerebrum.phonesensor.phone.sensors.notification.NotificationService;

import java.util.List;

public class Main2Activity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    Callback callbackAppUsage = new Callback() {
        @Override
        public void onSuccess() {
            PermissionInfo permissionInfo = new PermissionInfo();
            permissionInfo.getPermissions(Main2Activity.this, new ResultCallback<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (!result) {
                        MCerebrum.setPermission(Main2Activity.this, false);
                        Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }

        @Override
        public void onDenied() {
            Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
    Callback callbackNotification = new Callback() {
        @Override
        public void onSuccess() {
            checkAppUsagePermission();
        }

        @Override
        public void onDenied() {
            Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    Callback callbackOverlay = new Callback() {
        @Override
        public void onSuccess() {
            checkNotificationPermission();
        }

        @Override
        public void onDenied() {
            Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

/*

    Callback callbackSMS = new Callback() {
        @Override
        public void onSuccess() {
            checkAppUsagePermission();
        }

        @Override
        public void onDenied() {
            Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sharedpreferences = getSharedPreferences("Mine", Context.MODE_PRIVATE);
        if (!isAgree()) {
            Button ba = (Button) findViewById(R.id.buttonAgree);

            ba.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAgree();
                    checkOverlayPermission();
                }
            });
            Button bb = (Button) findViewById(R.id.buttonDisagree);
            bb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Disagreed..Could not continue...", Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        } else {
            checkOverlayPermission();
        }
    }

    @SuppressWarnings("ResourceType")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean hasAppUsagePermission() {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
        final List<UsageStats> queryUsageStats =
                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                        System.currentTimeMillis());

        if (queryUsageStats.isEmpty()) {
            return false;

        } else {
            return true;
        }
    }

    void checkAppUsagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @SuppressLint("WrongConstant") UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

            if ((stats == null || stats.isEmpty()) && !hasAppUsagePermission()) {
                Intent myIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//            myIntent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 102);
            } else callbackAppUsage.onSuccess();
        } else callbackAppUsage.onSuccess();

    }

    boolean hasNotificationPermission() {
        ComponentName cn = new ComponentName(this, NotificationService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());

    }

    void checkNotificationPermission() {
        if (hasNotificationPermission()) {
            callbackNotification.onSuccess();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, 103);
        }
    }
/*
    void checkSMSPermission() {
        if (hasSMSPermission()) {
            callbackSMS.onSuccess();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, 103);
        }
    }
*/

    boolean isAgree() {
        return sharedpreferences.getBoolean("agree", false);
    }

    void setAgree() {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean("agree", true);
        editor.apply();
    }

    void checkOverlayPermission() {
        if (hasOverlayPermission()) callbackOverlay.onSuccess();
        else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 101);
            }
        }
    }

    boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        } else return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (hasOverlayPermission()) callbackOverlay.onSuccess();
            else callbackOverlay.onDenied();
        }
        if (requestCode == 102) {
            if (hasAppUsagePermission()) {
                callbackAppUsage.onSuccess();
            } else callbackAppUsage.onDenied();
        }
        if (requestCode == 103) {
            if (hasNotificationPermission()) {
                callbackNotification.onSuccess();
            } else callbackNotification.onDenied();
        }

    }

}
