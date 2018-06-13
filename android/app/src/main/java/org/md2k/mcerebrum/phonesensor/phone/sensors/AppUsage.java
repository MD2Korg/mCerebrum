package org.md2k.mcerebrum.phonesensor.phone.sensors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeString;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

//this service captures user's app usage when the phone is unlocked by the user
public class AppUsage extends PhoneSensorDataSource {

    public AppUsage(Context context) {
        super(context, DataSourceType.CU_APPUSAGE);
        frequency = "ON_CHANGE";
    }

    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        super.register(dataSourceBuilder, newCallBack);
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(screenLockReceiver, screenFilter);
    }

    public void unregister() {
        if (screenLockReceiver != null)
            context.unregisterReceiver(screenLockReceiver);
        if (mAppUsageHandler != null & mAppUsageTask != null)
            mAppUsageHandler.removeCallbacks(mAppUsageTask);

    }

    // screen lock receiver
    private final BroadcastReceiver screenLockReceiver = new BroadcastReceiver() {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            boolean unlock = false;
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                unlock = true;
            }


            mAppUsageHandler.removeCallbacks(mAppUsageTask);

            if (unlock) {
                mAppUsageHandler.postDelayed(mAppUsageTask, FIRSTCAPTUREDELAY);
            }
        }
    };

    @SuppressWarnings("ResourceType")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean doIHavePermission() {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        final List<UsageStats> queryUsageStats =
                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                        System.currentTimeMillis());

        if (queryUsageStats.isEmpty()) {
            return false;

        } else {
            return true;
        }
    }

    @SuppressWarnings("ResourceType")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void captureAppUsageLollipop() {
        String topPackageName;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

        // Sort the stats by the last time used
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                final String record = topPackageName;
                DataTypeString appStatus = new DataTypeString(DateTime.getDateTime(), record);
                try {
                    dataKitAPI.insert(dataSourceClient, appStatus);
                } catch (DataKitException e) {
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    private void captureAppUsageOther() {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);

        if (taskList.size() > 0) {
            try {
                ActivityManager.RunningTaskInfo taskInfo = am
                        .getRunningTasks(1).get(0);
                final String packageName = taskInfo.topActivity.getPackageName();
                int runningActivityNum = taskInfo.numRunning;

                long now = System.currentTimeMillis();

                String record = packageName + "," + runningActivityNum;
                //ML_toolkit_object runningTask = appState.mMlToolkitObjectPool
                //        .borrowObject();
                //runningTask.setValues(now,
                //        EventDefinition.EVENT_RUNNINGTASK,
                //        record.getBytes());
                //appState.ML_toolkit_buffer.insert(runningTask);

                try {
                    DataTypeString appStatus = new DataTypeString(DateTime.getDateTime(), packageName);
                    dataKitAPI.insert(dataSourceClient, appStatus);

                } catch (DataKitException e) {

                    e.printStackTrace();
                }

            } catch (Exception ex) {

            }
        }
    }

    private void captureAppUsage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureAppUsageLollipop();
        } else {
            captureAppUsageOther();
        }
    }

    private final int FIRSTCAPTUREDELAY = 5 * 1000;
    private final int CAPTUREINTERVAL = 1 * 60 * 1000;
    private Handler mAppUsageHandler = new Handler();
    private Runnable mAppUsageTask = new Runnable() {
        public void run() {
            mAppUsageHandler.removeCallbacks(mAppUsageTask);
            captureAppUsage();
            mAppUsageHandler.postDelayed(mAppUsageTask, CAPTUREINTERVAL);
        }
    };

}
