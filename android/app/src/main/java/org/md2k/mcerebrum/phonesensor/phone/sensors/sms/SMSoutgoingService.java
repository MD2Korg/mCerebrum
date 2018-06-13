package org.md2k.mcerebrum.phonesensor.phone.sensors.sms;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
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


public class SMSoutgoingService extends Service {

    private int id = 0;
    private Context appState;
    private DataSourceClient dataSourceClient = null;
    private static final String TAG = "SMSoutgoingService";
    String smsphonenumber;
    long smstimestamp;
    int  smslen, smstype;
    private Activity activity;
    Context context;

    DataKitAPI dataKitAPI;


    public static final String ACTION = "org.bewellapp.ServiceControllers.SMSoutgoingService";

    public SMSoutgoingService() {

    }


    public void onCreate() {
        Log.i("SMSoutgoing", "SMSoutgoing Created!!");
        super.onCreate();

        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"),true, new SmsObserver(new Handler(),activity));
        appState = MainApplication.getContext();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }



    /* this method handles a single incoming request */
    @Override
    public int onStartCommand(Intent intent, int flags, int id) {

        //Log.d("OUTGOING", "RUNNING SERVICE");

        return START_STICKY; // stay running
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null; // disable binding
    }

    public class SmsObserver extends ContentObserver {
        Activity mActivity;
        public SmsObserver(Handler handler, Activity mActivity) {
            super(handler);
            this.mActivity= mActivity;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //Log.e("SMSoutgoing1", "SMSoutgoing Onchanged");
            Uri uriSMSURI = Uri.parse("content://sms/");
            Cursor cr = SMSoutgoingService.this.getContentResolver().query(uriSMSURI, null, null, null, null);
            //Log.e("SMSoutgoing1", "SMSoutgoing cursor");
            cr.moveToNext();
            if (cr.getInt(cr.getColumnIndex("type")) == 2) {
                if(id != cr.getInt(cr.getColumnIndex(cr.getColumnName(0)))) {
                    id = cr.getInt(cr.getColumnIndex(cr.getColumnName(0)));
                    //Log.e("SMSoutgoing1", "SMSoutgoing msg");
                    smsphonenumber = sha256(cr.getString(cr.getColumnIndex("address")));
                    smstimestamp = cr.getLong(cr.getColumnIndexOrThrow("date"));
                    smslen = (cr.getString(cr.getColumnIndex("body"))).length();
                    smstype = 2;

                    //Database.messageSent(SmsOutgoingObserver.this, address);
                    Log.i("OUTGOINGSMS",  smsphonenumber + "time" + smstimestamp + "length" + smslen + "type" + smstype);
                    //Toast toast = Toast.makeText(mActivity,"Incoming senderNum: "+  smstimestamp + smsphonenumber + ", message: " + smslen, 10);
                    //toast.show();

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
                                    DataTypeString number = new DataTypeString(DateTime.getDateTime(), smsphonenumber);

                                    dataKitAPI.insert(dataSourceClient, number);
                                    Log.i(TAG, "SMS outgoing number number="+number);

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
                                    Log.i(TAG, "SMS outgoing type type="+type);

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
                                    Log.i(TAG, "SMS outgoing length len="+len);

                                } catch (DataKitException e) {
                                    e.printStackTrace();
                                    Log.i(TAG, "SMS outgoing datakit Error");
                                }
                            }
                        });
                    } catch (DataKitException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i("OUTGOING1", "MESSAGE ALREADY LOGGED");
                }
            };
            cr.close();
        }



            //Database.messageReceived(SmsOutgoingObserver.this, address);

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




}

