package org.md2k.mcerebrum.phonesensor.phone.sensors;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.METADATA;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.phonesensor.ServicePhoneSensor;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;
import org.md2k.mcerebrum.core.data_format.DataFormat;;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public class Battery extends PhoneSensorDataSource {
    private Handler scheduler;
    private final Runnable batteryStatus = new Runnable() {

        @Override
        public void run() {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = context.registerReceiver(null, iFilter);
            assert intent != null;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            float percentage;
            if (level == -1 || scale == -1) {
                percentage = 0.0f;
            } else {
                percentage = ((float) level / (float) scale) * 100.0f;
            }
            double samples[] = new double[3];
            samples[DataFormat.Battery.Parcentage] = percentage;
            samples[DataFormat.Battery.Voltage] = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            samples[DataFormat.Battery.Temperature] = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            DataTypeDoubleArray dataTypeDoubleArray = new DataTypeDoubleArray(DateTime.getDateTime(), samples);
            try {
                dataKitAPI.insert(dataSourceClient, dataTypeDoubleArray);
                callBack.onReceivedData(dataTypeDoubleArray);
                scheduler.postDelayed(batteryStatus, 1000);
            } catch (DataKitException e) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ServicePhoneSensor.INTENT_STOP));
            }
        }
    };

    public Battery(Context context) {
        super(context, DataSourceType.BATTERY);
        frequency = "1.0";
    }


    public void unregister() {
        if (scheduler != null) {
            scheduler.removeCallbacks(batteryStatus);
            scheduler = null;
        }
    }

    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        super.register(dataSourceBuilder, newCallBack);
        scheduler = new Handler();
        scheduler.post(batteryStatus);
    }
}
