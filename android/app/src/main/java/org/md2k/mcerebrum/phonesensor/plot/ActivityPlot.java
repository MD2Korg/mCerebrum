package org.md2k.mcerebrum.phonesensor.plot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloat;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.mcerebrum.core.datakitapi.source.METADATA;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.commons.plot.RealtimeLineChartActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityPlot extends RealtimeLineChartActivity {
    String dataSourceType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSourceType = getIntent().getStringExtra("datasourcetype");
        if(dataSourceType==null) finish();

    }
    @Override
    public void onResume(){
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("phonesensor"));

        super.onResume();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePlot(intent);
        }
    };
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
    }
    void updatePlot(Intent intent) {
        float[] sample=new float[1];
        String[] legends;
        DataSource dataSource= intent.getParcelableExtra("datasource");
        ArrayList<HashMap<String, String>> hm = dataSource.getDataDescriptors();
        getmChart().getDescription().setText(dataSourceType);
        getmChart().getDescription().setPosition(1f,1f);
        getmChart().getDescription().setEnabled(true);
        getmChart().getDescription().setTextColor(Color.WHITE);
        legends=new String[hm.size()];
        for(int i=0;i<hm.size();i++){
            legends[i] = hm.get(i).get(METADATA.NAME);
        }
        String curDataSourceType = dataSource.getType();
        if(!curDataSourceType.equals(dataSourceType)) return;
        DataType data = intent.getParcelableExtra("data");
        if (data instanceof DataTypeFloat) {
            sample = new float[]{((DataTypeFloat) data).getSample()};
        } else if (data instanceof DataTypeFloatArray) {
            sample = ((DataTypeFloatArray) data).getSample();
        } else if (data instanceof DataTypeDoubleArray) {
            double[] samples = ((DataTypeDoubleArray) data).getSample();
            sample=new float[samples.length];
            for (int i = 0; i < samples.length; i++) {
                sample[i]= (float) samples[i];
            }
        }
        addEntry(sample, legends,600);
    }

}
