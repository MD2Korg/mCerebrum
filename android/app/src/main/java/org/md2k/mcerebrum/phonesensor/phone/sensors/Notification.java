package org.md2k.mcerebrum.phonesensor.phone.sensors;

/**
 * Created by akanes on 10/10/2017.
 */

import android.content.Context;
import android.content.Intent;

import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.phonesensor.phone.CallBack;
import org.md2k.mcerebrum.phonesensor.phone.sensors.notification.NotificationService;

//this service captures user's app usage when the phone is unlocked by the user
public class Notification extends PhoneSensorDataSource {

    public Notification(Context context) {
        super(context, DataSourceType.CU_NOTIF_POST_PACKAGE);
        frequency = "ON_CHANGE";
    }

    public void register(DataSourceBuilder dataSourceBuilder, CallBack newCallBack) throws DataKitException {
        super.register(dataSourceBuilder, newCallBack);
        Intent intent = new Intent(context, NotificationService.class);
        context.startService(intent);
    }

    public void unregister() {
        Intent intent = new Intent(context, NotificationService.class);
        context.stopService(intent);
    }

}

