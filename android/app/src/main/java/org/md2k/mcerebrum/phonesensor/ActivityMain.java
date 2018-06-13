package org.md2k.mcerebrum.phonesensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloat;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.BuildConfig;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.phonesensor.phone.sensors.PhoneSensorDataSource;
import org.md2k.mcerebrum.phonesensor.phone.sensors.PhoneSensorDataSources;
import org.md2k.mcerebrum.phonesensor.plot.ActivityPlotChoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
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

public class ActivityMain extends AppCompatActivity {
    HashMap<String, TextView> hashMapData = new HashMap<>();
    boolean isEverythingOk = false;
    Handler mHandler;
    int operation;

    public static final int OPERATION_RUN = 0;
    public static final int OPERATION_SETTINGS = 1;
    public static final int OPERATION_PLOT = 2;
    public static final int OPERATION_START_BACKGROUND=3;
    public static final int OPERATION_STOP_BACKGROUND=4;
    public static final String OPERATION = "operation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCrashlytics();
        readIntent();
        initializeVariable();
        checkRequirement();
    }

    void load() {
        isEverythingOk = true;
        Intent intent;
        switch (operation) {
            case OPERATION_RUN:
                initializeUI();
                break;
            case OPERATION_START_BACKGROUND:
                intent = new Intent(ActivityMain.this, ServicePhoneSensor.class);
                startService(intent);
                finish();
                break;
            case OPERATION_STOP_BACKGROUND:
                intent = new Intent(ActivityMain.this, ServicePhoneSensor.class);
                stopService(intent);
                finish();
                break;
            case OPERATION_PLOT:
                intent = new Intent(this, ActivityPlotChoice.class);
                intent.putExtra("datasourcetype", getIntent().getStringExtra("datasourcetype"));
                startActivity(intent);
                finish();
                break;
            case OPERATION_SETTINGS:
                intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                finish();
                break;
            default:
//                Toasty.error(getApplicationContext(), "Invalid argument. Operation = " + operation, Toast.LENGTH_SHORT).show();
                initializeUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                break;
            case R.id.action_plot:
                intent = new Intent(this, ActivityPlotChoice.class);
                startActivity(intent);
                break;
            case R.id.action_location:
                intent = new Intent(this, ActivitySettingsGeofence.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    TableRow createDefaultRow() {
        TableRow row = new TableRow(this);
        TextView tvSensor = new TextView(this);
        tvSensor.setText(R.string.table_sensor_id);
        tvSensor.setTypeface(null, Typeface.BOLD);
        tvSensor.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvCount = new TextView(this);
        tvCount.setText(R.string.table_header_count);
        tvCount.setTypeface(null, Typeface.BOLD);
        tvCount.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvFreq = new TextView(this);
        tvFreq.setText(R.string.table_header_freq);
        tvFreq.setTypeface(null, Typeface.BOLD);
        tvFreq.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvSample = new TextView(this);
        tvSample.setText(R.string.table_header_samples);
        tvSample.setTypeface(null, Typeface.BOLD);
        tvSample.setTextColor(getResources().getColor(R.color.teal_A700));
        row.addView(tvSensor);
        row.addView(tvCount);
        row.addView(tvFreq);
        row.addView(tvSample);
        return row;
    }

    void prepareTable() {
        try {
            ArrayList<PhoneSensorDataSource> phoneSensorDataSources = new PhoneSensorDataSources(getApplicationContext()).getPhoneSensorDataSources();
            TableLayout ll = (TableLayout) findViewById(R.id.tableLayout);
            ll.removeAllViews();
            ll.addView(createDefaultRow());
            for (int i = 0; i < phoneSensorDataSources.size(); i++) {
                if (phoneSensorDataSources.get(i).isEnabled()) {
                    String dataSourceType = phoneSensorDataSources.get(i).getDataSourceType();
                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    TextView tvSensor = new TextView(this);
                    tvSensor.setPadding(10, 0, 0, 0);
                    try {
                        tvSensor.setText(dataSourceType.toLowerCase() + " (" + phoneSensorDataSources.get(i).getFrequency() + ")");
                    } catch (NumberFormatException nfe) {
                        tvSensor.setText(dataSourceType.toLowerCase() + " (" + phoneSensorDataSources.get(i).getFrequency() + ")");
                    }

                    TextView tvCount = new TextView(this);
                    tvCount.setText("0");
                    hashMapData.put(dataSourceType + "_count", tvCount);
                    TextView tvFreq = new TextView(this);
                    tvFreq.setText("0");
                    hashMapData.put(dataSourceType + "_freq", tvFreq);
                    TextView tvSample = new TextView(this);
                    tvSample.setText("0");
                    hashMapData.put(dataSourceType + "_sample", tvSample);
                    row.addView(tvSensor);
                    row.addView(tvCount);
                    row.addView(tvFreq);
                    row.addView(tvSample);
                    row.setBackgroundResource(R.drawable.border);
                    ll.addView(row);
                }
            }
        }catch (Exception e){

        }
    }

    void updateTable(Intent intent) {
        String sampleStr = "";
        String dataSourceType = ((DataSource) intent.getParcelableExtra("datasource")).getType();
        int count = intent.getIntExtra("count", 0);
        hashMapData.get(dataSourceType + "_count").setText(String.valueOf(count));

        double time = (intent.getLongExtra("timestamp", 0) - intent.getLongExtra("starttimestamp", 0)) / 1000.0;
        double freq = (double) count / time;
        hashMapData.get(dataSourceType + "_freq").setText(String.format(Locale.getDefault(), "%.1f", freq));


        DataType data = intent.getParcelableExtra("data");

        if (data instanceof DataTypeFloat) {
            sampleStr = String.format(Locale.getDefault(), "%.1f", ((DataTypeFloat) data).getSample());
        } else if (data instanceof DataTypeFloatArray) {
            float[] sample = ((DataTypeFloatArray) data).getSample();
            for (int i = 0; i < sample.length; i++) {
                if (i != 0) sampleStr += ",";
                if (i % 3 == 0 && i != 0) sampleStr += "\n";
                sampleStr = sampleStr + String.format(Locale.getDefault(), "%.1f", sample[i]);
            }
        } else if (data instanceof DataTypeDoubleArray) {
            double[] sample = ((DataTypeDoubleArray) data).getSample();
            for (int i = 0; i < sample.length; i++) {
                if (i != 0) sampleStr += ",";
                if (i % 3 == 0 && i != 0) sampleStr += "\n";
                sampleStr = sampleStr + String.format(Locale.getDefault(), "%.1f", sample[i]);
            }
        }
        hashMapData.get(dataSourceType + "_sample").setText(sampleStr);
    }

    @Override
    public void onResume() {
        try {
            if (isEverythingOk) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                        new IntentFilter("phonesensor"));
                prepareTable();
                mHandler.post(runnable);
            }
        }catch (Exception e){

        }
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            if (isEverythingOk) {
                mHandler.removeCallbacks(runnable);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            }
        }catch (Exception ignored){

        }
        super.onPause();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            {
                long time = AppInfo.serviceRunningTime(ActivityMain.this, ServicePhoneSensor.class.getName());
                if (time < 0) {
                    ((Button) findViewById(R.id.button_app_status)).setText("START");
                    findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_off));

                } else {
                    findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_on));
                    ((Button) findViewById(R.id.button_app_status)).setText(DateTime.convertTimestampToTimeStr(time));

                }
                mHandler.postDelayed(this, 1000);
            }
        }
    };
    ResponseCallback status = new ResponseCallback() {
        @Override
        public void onResponse(boolean result) {
            if (!result)
                finish();
            else {
                load();
            }
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTable(intent);
        }
    };


    void checkRequirement() {
        Intent intent = new Intent(this, ActivityPermission.class);
        startActivityForResult(intent, 1111);
/*
        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    status.onResponse(false);
                } else {
                    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                    int result = googleAPI.isGooglePlayServicesAvailable(ActivityMain.this);
                    if (result != ConnectionResult.SUCCESS) {
                        if (googleAPI.isUserResolvableError(result)) {
                            googleAPI.getErrorDialog(ActivityMain.this, result,
                                    9000, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            Toasty.error(getApplicationContext(), "Google play service is disabled/not updated", Toast.LENGTH_SHORT).show();
                                            status.onResponse(false);
                                        }
                                    }).show();
                        } else {
                            Toasty.error(getApplicationContext(), "Google play service is DISABLED/NOT UPDATED", Toast.LENGTH_SHORT).show();
                            status.onResponse(false);
                        }
                    } else {
                        status.onResponse(true);
                    }
                }

            }
        });
*/
    }

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9000) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int result = googleAPI.isGooglePlayServicesAvailable(ActivityMain.this);
            if (result != ConnectionResult.SUCCESS) {
                Toasty.error(getApplicationContext(), "Google play service is disabled/not updated", Toast.LENGTH_SHORT).show();
                status.onResponse(false);
            } else {
                status.onResponse(true);
            }
        }
    }
*/
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1111) {
        if(resultCode!=RESULT_OK){
            finish();
        }else{
            load();
        }
    }
}

    void loadCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit, new Crashlytics());
    }

    void readIntent() {
        if(getIntent().getExtras()!=null)
        operation = getIntent().getExtras().getInt(OPERATION, 0);
        else operation=0;
    }

    void initializeVariable() {
        isEverythingOk = false;
        mHandler = new Handler();
    }

    void initializeUI() {
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Button buttonService = (Button) findViewById(R.id.button_app_status);
        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ServicePhoneSensor.class);
                if (AppInfo.isServiceRunning(getBaseContext(), ServicePhoneSensor.class.getName())) {
                    stopService(intent);
                } else {
                    startService(intent);
                }
            }
        });

    }
}

