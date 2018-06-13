package org.md2k.mcerebrum.phonesensor.phone.sensors.sms;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SMSincomingService extends Service {

    Context context;
    Activity act;
    ListView lvsms;
    public static String msg = "msg", phoneNo = "phoneNo", time = "time";

    public static String typeMsg = "0";
    public static String typeSend = "1";
// String typeDeliver = "2";


    String smsphoneNumber;
    long smstimestamp;
    int  smslen, smstype;

    //TextView smsno_record;


    @Override
    public IBinder onBind(Intent intent) {
        return null; // disable binding
    }

    BroadcastReceiver onNewMsgReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("SMSincoming", "onNewMsgReceive ");

        }
    };

   // BroadcastReceiver onNewMsgSend = new BroadcastReceiver() {
   //     @Override
    //     public void onReceive(Context context, Intent intent) {
    //        Log.i("CommActivity", "onNewMsgSend");
    //    }
    // };

// BroadcastReceiver deliveredreceiver = new BroadcastReceiver() {
// @Override
// public void onReceive(Context context, Intent intent) {
//
// }
// };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onNewMsgReceive);
        //unregisterReceiver(onNewMsgSend);
        // unregisterReceiver(deliveredreceiver);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate();

        registerReceiver(onNewMsgReceive, new IntentFilter("onNewMsgReceive"));
        //registerReceiver(onNewMsgSend, new IntentFilter("onNewMsgSend"));
        // registerReceiver(deliveredreceiver, new IntentFilter(
        // "deliveredreceiver"));
        //setContentView(R.layout.complete_sms_data);
        context = SMSincomingService.this;
        //act = SMSincomingService.this;

        //lvsms = (ListView) findViewById(R.id.lvsms);
        //smsno_record = (TextView) findViewById(R.id.smsno_record);
        //smsdetails(typeMsg);//      sendboxSMS();
        Log.e("SMSincoming", "SMSincoming Created!!");
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                //action for sms received
                Bundle bundle=intent.getExtras();

                //void handleIncomingMsg(Bundle bundle, Context context) {
                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    //Database db = new Database(context);
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage
                                .createFromPdu((byte[]) pdusObj[i]);
                        smstimestamp = currentMessage.getTimestampMillis();
                        smsphoneNumber = currentMessage.getDisplayOriginatingAddress();
                        smslen = (currentMessage.getDisplayMessageBody()).length();
                        smstype = 1;
                        Log.i("SmsReceiver", "Incoming timestamp" + smstimestamp + "senderNum: " + smsphoneNumber + "; messagelength: "
                                + smslen+ "type" + smstype);
                        //db.insertRecord(senderNum, message, CommActivity.typeMsg);

                        //Toast toast = Toast.makeText(context, "Incoming senderNum: "+  smstimestamp + smsphoneNumber + ", message: " + smslen, 10);
                        //toast.show();
                    }



                   // context.sendBroadcast(new Intent("onNewMsg"));
                    //db.close();
                //  }
            }

        }
    };
}