package org.md2k.mcerebrum.phonesensor.plot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.phonesensor.phone.sensors.PhoneSensorDataSources;

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
public class PrefsFragmentPlot extends PreferenceFragment {
    PhoneSensorDataSources phoneSensorDataSources;
/*
    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            phoneSensorDataSources.find(preference.getKey()).setEnabled((Boolean) newValue);
            updatePreferenceScreen();
            return false;
        }
    };
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readConfiguration();
        addPreferencesFromResource(R.xml.pref_plot_choice);
        createPreferenceScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }

    void createPreferenceScreen() {
        addPreferenceScreenSensors();
    }

    void readConfiguration() {
        phoneSensorDataSources = new PhoneSensorDataSources(getActivity());
    }

    boolean isSensorSupported(String dataSourceType) {
        SensorManager mSensorManager;
        Sensor mSensor;
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        switch (dataSourceType) {
            case DataSourceType.ACCELEROMETER:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                break;
            case (DataSourceType.GYROSCOPE):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                break;
            case (DataSourceType.AMBIENT_TEMPERATURE):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                break;
            case (DataSourceType.COMPASS):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
                break;
            case (DataSourceType.AMBIENT_LIGHT):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                break;
            case (DataSourceType.PRESSURE):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                break;
            case (DataSourceType.PROXIMITY):
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                break;
            case DataSourceType.LOCATION:
                return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            default:
                return true;

        }
        return mSensor != null;
    }

    private Preference createPreference(String dataSourceType) {

        Preference preference = new Preference(getActivity());
        preference.setKey(dataSourceType);
        String title = dataSourceType;
        title = title.replace("_", " ");
        title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
        preference.setTitle(title);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent(getActivity(), ActivityPlot.class);
                intent.putExtra("datasourcetype", preference.getKey());
                startActivity(intent);
                return false;
            }
        });
        preference.setEnabled(isSensorSupported(dataSourceType));
        return preference;
    }

    protected void addPreferenceScreenSensors() {
        String dataSourceType;
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("dataSourceType");
        preferenceCategory.removeAll();
        for (int i = 0; i < phoneSensorDataSources.getPhoneSensorDataSources().size(); i++) {
            dataSourceType = phoneSensorDataSources.getPhoneSensorDataSources().get(i).getDataSourceType();
            if(!phoneSensorDataSources.getPhoneSensorDataSources().get(i).isEnabled()) continue;
            Preference preference = createPreference(dataSourceType);
            preferenceCategory.addPreference(preference);
        }
    }
}
