package org.md2k.mcerebrum.phonesensor.phone.sensors;

/**
 * Created by akanes on 10/10/2017.
 */

import android.content.Context;
import android.content.Intent;

import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;
import org.md2k.mcerebrum.phonesensor.phone.sensors.sms.SMSoutgoingService;

//this service captures user's app usage when the phone is unlocked by the user
public class SMS extends PhoneSensorDataSource {

    public SMS(Context context) {
        super(context, "CU_SMS_TYPE");
        frequency = "ON_CHANGE";
    }

    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        super.register(dataSourceBuilder, newCallBack);
        Intent intent = new Intent(SMSoutgoingService.ACTION);
        intent.setClass(context, SMSoutgoingService.class);
        context.startService(intent);
    }

    public void unregister() {
        Intent intent = new Intent(SMSoutgoingService.ACTION);
        intent.setClass(context, SMSoutgoingService.class);
        context.stopService(intent);
    }

}

