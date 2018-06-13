package org.md2k.mcerebrum.phonesensor.phone.sensors;

/**
 * Created by akanes on 9/29/2017.
 */

import android.content.Context;

import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;

public class CallReceiver extends PhoneSensorDataSource {

    public CallReceiver(Context context) {
        super(context, DataSourceType.CU_CALL_TYPE);
        frequency = "ON_CHANGE";
    }

    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        super.register(dataSourceBuilder, newCallBack);
    }

    public void unregister() {
    }
}