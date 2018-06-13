package org.md2k.mcerebrum.phonesensor.phone.sensors.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.md2k.mcerebrum.MainApplication;
import org.md2k.mcerebrum.core.datakitapi.DataKitAPI;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeInt;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeString;
import org.md2k.mcerebrum.core.datakitapi.exception.DataKitException;
import org.md2k.mcerebrum.core.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.mcerebrum.core.datakitapi.source.METADATA;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by akanes on 9/29/2017.
 */


public class SMSIncomingReceiver extends BroadcastReceiver
{

        private static final String TAG = "SMSincomingReceiver";

        String smsphoneNumber;
        long smstimestamp;
        int  smslen, smstype;
        Context context;

        DataKitAPI dataKitAPI;

        @Override
        public void onReceive(Context context, Intent intent) {

                //registerClient();

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
                smsphoneNumber = sha256(currentMessage.getDisplayOriginatingAddress());
                smslen = (currentMessage.getDisplayMessageBody()).length();
                smstype = 1;
                Log.i("SmsReceiver", "Incoming timestamp" + smstimestamp + "senderNum: " + smsphoneNumber + "; messagelength: "
                + smslen+ "type" + smstype);
                //db.insertRecord(senderNum, message, CommActivity.typeMsg);





                        dataKitAPI = DataKitAPI.getInstance(context);
                        try {
                                dataKitAPI.connect(new OnConnectionListener() {
                                        @Override
                                        public void onConnected() {
                                                try {
                                                        HashMap<String, String> dd1=new HashMap<>();
                                                        dd1.put(METADATA.NAME,"SMS Number");
                                                        dd1.put(METADATA.DESCRIPTION,"SMS phone number. The actual number is not saved. SHA256 cryptographic hash function is used to convert the number to a string for privacy reason");
                                                        dd1.put(METADATA.FREQUENCY ,"ON_CHANGE");
                                                        dd1.put(METADATA.UNIT,"string");
                                                        dd1.put(METADATA.DATA_TYPE,"String");
                                                        ArrayList<HashMap<String, String>> dss1=new ArrayList<>();
                                                        dss1.add(dd1);

                                                        DataSourceBuilder dataSourceBuilder1 = new DataSourceBuilder().setType("CU_SMS_NUMBER");
                                                        dataSourceBuilder1 = dataSourceBuilder1
                                                                .setMetadata(METADATA.NAME,"SMS Number")
                                                                .setMetadata(METADATA.DESCRIPTION,"SMS phone number. The actual number is not saved. SHA256 cryptographic hash function is used to convert the number to a string for privacy reason")
                                                                .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                                                                .setMetadata(METADATA.UNIT,"string")
                                                                .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeString");
                                                        dataSourceBuilder1 = dataSourceBuilder1.setDataDescriptors(dss1);

                                                        DataSourceClient dataSourceClient = dataKitAPI.register(dataSourceBuilder1);
                                                        DataTypeString number = new DataTypeString(DateTime.getDateTime(), smsphoneNumber);

                                                        dataKitAPI.insert(dataSourceClient, number);
                                                        Log.i(TAG, "SMS number number = "+number);



                                                        HashMap<String, String> dd2=new HashMap<>();
                                                        dd2.put(METADATA.NAME,"SMS Type");
                                                        dd2.put(METADATA.DESCRIPTION,"SMS Type (1: incoming, 2: outgoing)");
                                                        dd2.put(METADATA.FREQUENCY ,"ON_CHANGE");
                                                        dd2.put(METADATA.UNIT,"enum");
                                                        dd2.put(METADATA.DATA_TYPE,"int");
                                                        ArrayList<HashMap<String, String>> dss2=new ArrayList<>();
                                                        dss2.add(dd2);

                                                        DataSourceBuilder dataSourceBuilder2 = new DataSourceBuilder().setType("CU_SMS_TYPE");
                                                        dataSourceBuilder2 = dataSourceBuilder2
                                                                .setMetadata(METADATA.NAME,"SMS Type")
                                                                .setMetadata(METADATA.DESCRIPTION,"SMS Type (1: incoming, 2: outgoing)")
                                                                .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                                                                .setMetadata(METADATA.UNIT,"enum")
                                                                .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeInt");
                                                        dataSourceBuilder2 = dataSourceBuilder2.setDataDescriptors(dss2);

                                                        DataSourceClient dataSourceClient2 = dataKitAPI.register(dataSourceBuilder2);
                                                        DataTypeInt type = new DataTypeInt(DateTime.getDateTime(), smstype);

                                                        dataKitAPI.insert(dataSourceClient2, type);
                                                        Log.i(TAG, "SMS type ="+type);


                                                        HashMap<String, String> dd3=new HashMap<>();
                                                        dd3.put(METADATA.NAME,"SMS Length");
                                                        dd3.put(METADATA.DESCRIPTION,"SMS message (body) length");
                                                        dd3.put(METADATA.FREQUENCY ,"ON_CHANGE");
                                                        dd3.put(METADATA.UNIT,"number");
                                                        dd3.put(METADATA.DATA_TYPE,"int");
                                                        ArrayList<HashMap<String, String>> dss3=new ArrayList<>();
                                                        dss3.add(dd3);

                                                        DataSourceBuilder dataSourceBuilder3 = new DataSourceBuilder().setType("CU_SMS_LENGTH");
                                                        dataSourceBuilder3 = dataSourceBuilder3
                                                                .setMetadata(METADATA.NAME,"SMS Length")
                                                                .setMetadata(METADATA.DESCRIPTION,"SMS message (body) length")
                                                                .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                                                                .setMetadata(METADATA.UNIT,"number")
                                                                .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeInt");
                                                        dataSourceBuilder3 = dataSourceBuilder3.setDataDescriptors(dss3);

                                                        DataSourceClient dataSourceClient3 = dataKitAPI.register(dataSourceBuilder3);
                                                        DataTypeInt len = new DataTypeInt(DateTime.getDateTime(), smslen);

                                                        dataKitAPI.insert(dataSourceClient3, len);
                                                        Log.i(TAG, "SMS length len="+len);

                                                } catch (DataKitException e) {
                                                        e.printStackTrace();
                                                        Log.i(TAG, "SMS datakit Error");
                                                }
                                        }
                                });
                        } catch (DataKitException e) {
                                e.printStackTrace();
                        }



                }
                // context.sendBroadcast(new Intent("onNewMsg"));
                //db.close();
                //  }
                }

        }


        public static String sha256(String base) {
                try{
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hash = digest.digest(base.getBytes("UTF-8"));
                        StringBuffer hexString = new StringBuffer();

                        for (int i = 0; i < hash.length; i++) {
                                String hex = Integer.toHexString(0xff & hash[i]);
                                if(hex.length() == 1) hexString.append('0');
                                hexString.append(hex);
                        }

                        return hexString.toString();
                } catch(Exception ex){
                        throw new RuntimeException(ex);
                }
        }


};



