package org.md2k.mcerebrum.phonesensor.phone.sensors;

import android.content.Context;
import android.util.Log;

import org.md2k.mcerebrum.core.datakitapi.DataKitAPI;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.METADATA;
import org.md2k.mcerebrum.core.datakitapi.source.application.Application;
import org.md2k.mcerebrum.core.datakitapi.source.application.ApplicationBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.source.platform.Platform;
import org.md2k.mcerebrum.phonesensor.Configuration;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;
import org.md2k.mcerebrum.phonesensor.phone.PhoneSensorPlatform;

import java.util.ArrayList;

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
public abstract class PhoneSensorDataSource {
    public static final double EPSILON_NORMAL = 2.0;
    public static final double EPSILON_UI = 5.0;
    public static final double EPSILON_GAME = 10.0;
    public static final double EPSILON_FASTEST = 50.0;
    private static final String TAG = PhoneSensorDataSource.class.getSimpleName();
    final Context context;
    private final String dataSourceType;
    DataSourceClient dataSourceClient;
    CallBack callBack;
    String frequency="SENSOR_DELAY_UI";
    DataKitAPI dataKitAPI;
    private boolean enabled;

    PhoneSensorDataSource(Context context, String dataSourceType) {
        this.context = context;
        this.dataSourceType = dataSourceType;
        this.enabled = false;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateDataSource(DataSource dataSource){
        enabled=true;
    }
    public String getFrequency(){
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    DataSourceBuilder createDataSourceBuilder() {
        if (!enabled) return null;
        DataSource dataSource  = Configuration.getMetaData(dataSourceType);
        return new DataSourceBuilder(dataSource).setType(dataSourceType).setMetadata(METADATA.FREQUENCY, frequency);
    }


    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        dataKitAPI = DataKitAPI.getInstance(context);
        dataSourceClient = dataKitAPI.register(dataSourceBuilder);
        callBack = newCallBack;
    }

    public abstract void unregister();
}
