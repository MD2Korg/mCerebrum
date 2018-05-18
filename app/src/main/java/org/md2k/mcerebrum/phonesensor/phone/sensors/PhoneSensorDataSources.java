package org.md2k.mcerebrum.phonesensor.phone.sensors;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.phonesensor.Configuration;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

public class PhoneSensorDataSources {
    private static final String TAG = PhoneSensorDataSources.class.getSimpleName();
    protected Context context;
    ArrayList<PhoneSensorDataSource> phoneSensorDataSources;
    HashMap<String, Integer> hm = new HashMap<>();
    long starttimestamp = 0;
//    PowerManager.WakeLock wl;
    public PhoneSensorDataSources(Context context) {
        this.context = context;
        phoneSensorDataSources = new ArrayList<>();
        phoneSensorDataSources.add(new Battery(context));
        phoneSensorDataSources.add(new LocationFused(context));
        phoneSensorDataSources.add(new ActivityType(context));
        phoneSensorDataSources.add(new Accelerometer(context));
        phoneSensorDataSources.add(new Gyroscope(context));
        phoneSensorDataSources.add(new Compass(context));
        phoneSensorDataSources.add(new AmbientLight(context));
        phoneSensorDataSources.add(new Pressure(context));
        phoneSensorDataSources.add(new AmbientTemperature(context));
        phoneSensorDataSources.add(new Proximity(context));
        phoneSensorDataSources.add(new CPU(context));
        phoneSensorDataSources.add(new Memory(context));
        phoneSensorDataSources.add(new StepCount(context));
        phoneSensorDataSources.add(new GeoFence(context));
        phoneSensorDataSources.add(new TouchScreen(context));
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        try {
            readDataSourceFromFile();
        } catch (FileNotFoundException e) {
//            Toast.makeText(context, "PhoneSensor Configuration file is not available.", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<PhoneSensorDataSource> getPhoneSensorDataSources() {
        return phoneSensorDataSources;
    }

    private void readDataSourceFromFile() throws FileNotFoundException {
        ArrayList<DataSource> dataSources = Configuration.read(context);
        assert dataSources != null;
        for (int i = 0; i < dataSources.size(); i++) {
            PhoneSensorDataSource phoneSensorDataSource = find(dataSources.get(i).getType());
            if (phoneSensorDataSource == null) continue;
            phoneSensorDataSource.updateDataSource(dataSources.get(i));
        }
    }

    public int countEnabled() {
        int count = 0;
        for (int i = 0; i < phoneSensorDataSources.size(); i++) {
            if (phoneSensorDataSources.get(i).isEnabled())
                count++;
        }

        return count;
    }

    public PhoneSensorDataSource find(String type) {
        for (int i = 0; i < phoneSensorDataSources.size(); i++) {
            if (phoneSensorDataSources.get(i).getDataSourceType().equals(type))
                return phoneSensorDataSources.get(i);
        }
        return null;
    }

    public void writeDataSourceToFile() throws IOException {
        ArrayList<DataSource> dataSources = new ArrayList<>();
        if (phoneSensorDataSources == null) throw new NullPointerException();
        if (phoneSensorDataSources.size() == 0) throw new EmptyStackException();
        for (int i = 0; i < phoneSensorDataSources.size(); i++) {
            if (!phoneSensorDataSources.get(i).isEnabled()) continue;
            DataSource dataSource = phoneSensorDataSources.get(i).createDataSourceBuilder().build();
            if (dataSource == null) continue;
            dataSources.add(dataSource);
        }
        Configuration.write(context, dataSources);
    }

    public void register() {
//        wl.acquire();
        hm.clear();
        starttimestamp = DateTime.getDateTime();
        for (int i = 0; i < phoneSensorDataSources.size(); i++) {
            if (!phoneSensorDataSources.get(i).isEnabled()) continue;
            final DataSourceBuilder dataSourceBuilder = phoneSensorDataSources.get(i).createDataSourceBuilder();
            if (dataSourceBuilder == null) continue;
            final int finalI = i;
            Intent intent = new Intent("phonesensor");
            intent.putExtra("operation", "register");
            intent.putExtra("datasource", (Parcelable) dataSourceBuilder.build());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            try {
                phoneSensorDataSources.get(i).register(dataSourceBuilder, new CallBack() {
                    @Override
                    public void onReceivedData(DataType data) {
                        String dataSourceType = phoneSensorDataSources.get(finalI).getDataSourceType();
                        Intent intent = new Intent("phonesensor");
                        intent.putExtra("operation", "data");
                        if (!hm.containsKey(dataSourceType)) {
                            hm.put(dataSourceType, 0);
                        }
                        hm.put(dataSourceType, hm.get(dataSourceType) + 1);
                        intent.putExtra("count", hm.get(dataSourceType));
                        intent.putExtra("timestamp", data.getDateTime());
                        intent.putExtra("starttimestamp", starttimestamp);
                        intent.putExtra("data", (Parcelable) data);
                        intent.putExtra("datasource", (Parcelable) dataSourceBuilder.build());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
            } catch (DataKitException e) {
                //TODO: Restart service?
                Toast.makeText(context, "Registration Error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void unregister() {
/*
        if (wl != null && wl.isHeld())
            wl.release();
*/
        if (phoneSensorDataSources != null) {
            for (int i = 0; i < phoneSensorDataSources.size(); i++) {
                if (!phoneSensorDataSources.get(i).isEnabled()) continue;
                phoneSensorDataSources.get(i).unregister();
            }
        }
        hm.clear();

    }
}
