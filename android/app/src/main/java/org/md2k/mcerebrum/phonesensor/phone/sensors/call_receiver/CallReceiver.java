package org.md2k.mcerebrum.phonesensor.phone.sensors.call_receiver;

/**
 * Created by akanes on 9/29/2017.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhonecallStartEndDetector listener;
    String outgoingSavedNumber;
    protected Context savedContext;
    private static final String TAG = "callreceiver";
    DataKitAPI dataKitAPI;

    @Override
    public void onReceive(Context context, Intent intent) {
        savedContext = context;
        if (listener == null) {
            listener = new PhonecallStartEndDetector();
        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Derived classes should override these to respond to specific events of interest
    private void onIncomingCallStarted(String number, Date start) {
    }

    ;

    private void onOutgoingCallStarted(String number, Date start) {
    }

    ;

    private void onIncomingCallEnded(String number, Date start, Date end) {
        Log.i("IncomingCall", "Ended!");
        getCallDetails(savedContext);
    }

    ;

    private void onOutgoingCallEnded(String number, Date start, Date end) {
        Log.i("OutgoingCall", "Ended!");
        getCallDetails(savedContext);
    }

    ;

    private void onMissedCall(String number, Date start) {
        Log.i("Missed call", "Ended!");
        getCallDetails(savedContext);
    }

    ;


    private void getCallDetails(Context context) {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.d("abc","Call permission...false");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor managedCursor = savedContext.getContentResolver().query(contacts, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        //sb.append("Call Details :");
        if (managedCursor.moveToLast()) {

            HashMap rowDataCall = new HashMap<String, String>();

            final String phNumber = sha256(managedCursor.getString(number));
            final String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDayTime = new Date(Long.valueOf(callDate)).toString();
            //long timestamp = convertDateToTimestamp(callDayTime);
            final String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            Log.i("call data", phNumber + "," + callType + dircode + "," +  callDate + "," + callDayTime + "," + callDuration);
            //sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            //sb.append("\n----------------------------------");

            dataKitAPI = DataKitAPI.getInstance(context);
            try {
                dataKitAPI.connect(new OnConnectionListener() {
                    @Override
                    public void onConnected() {
                        try {
                            HashMap<String, String> dataDescriptor1=new HashMap<>();
                            dataDescriptor1.put(METADATA.NAME,"Call Number");
                            dataDescriptor1.put(METADATA.DESCRIPTION,"Call phone number. The actual number is not saved. SHA256 cryptographic hash function is used to convert the number to a string for privacy reason");
                            dataDescriptor1.put(METADATA.FREQUENCY ,"ON_CHANGE");
                            dataDescriptor1.put(METADATA.UNIT,"String");
                            dataDescriptor1.put(METADATA.DATA_TYPE,"String");
                            ArrayList<HashMap<String, String>> dss1=new ArrayList<>();
                            dss1.add(dataDescriptor1);

                            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.CU_CALL_NUMBER);
                            dataSourceBuilder = dataSourceBuilder
                                    .setMetadata(METADATA.NAME,"Call Number")
                                    .setMetadata(METADATA.DESCRIPTION,"Call phone number. The actual number is not saved. SHA256 cryptographic hash function is used to convert the number to a string for privacy reason")
                                    .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                                    .setMetadata(METADATA.UNIT,"String")
                                    .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeString");
                            dataSourceBuilder = dataSourceBuilder.setDataDescriptors(dss1);

                            DataSourceClient dataSourceClient = dataKitAPI.register(dataSourceBuilder);
                            DataTypeString number = new DataTypeString(DateTime.getDateTime(), phNumber);

                            dataKitAPI.insert(dataSourceClient, number);
                            Log.i(TAG, "CALL number datakit");

                            DataSourceBuilder dataSourceBuilder2 = new DataSourceBuilder().setType(DataSourceType.CU_CALL_TYPE);
                            HashMap<String, String> dataDescriptor2=new HashMap<>();
                            dataDescriptor2.put(METADATA.UNIT,"enum");
                            dataDescriptor2.put(METADATA.NAME,"Call Type");
                            dataDescriptor2.put(METADATA.DESCRIPTION,"Type of call. [values: 1:INCOMING_TYPE, 2:OUTGOING_TYPE, 3:MISSED_TYPE, 4:VOICEMAIL_TYPE, 5:REJECTED_TYPE, 6:BLOCKED_TYPE, 7:ANSWERED_EXTERNALLY_TYPE]");
                            dataDescriptor2.put(METADATA.FREQUENCY ,"ON_CHANGE");
                            dataDescriptor2.put(METADATA.DATA_TYPE,"int");
                            ArrayList<HashMap<String, String>> dss2=new ArrayList<>();
                            dss2.add(dataDescriptor2);

                            dataSourceBuilder2 = dataSourceBuilder2
                            .setMetadata(METADATA.UNIT,"enum")
                            .setMetadata(METADATA.NAME,"Call Type")
                            .setMetadata(METADATA.DESCRIPTION,"Type of call. [values: 1:INCOMING_TYPE, 2:OUTGOING_TYPE, 3:MISSED_TYPE, 4:VOICEMAIL_TYPE, 5:REJECTED_TYPE, 6:BLOCKED_TYPE, 7:ANSWERED_EXTERNALLY_TYPE]")
                            .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                            .setMetadata(METADATA.NAME,"Call Type")
                            .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeInt");
                            dataSourceBuilder2 = dataSourceBuilder2.setDataDescriptors(dss2);

                            DataSourceClient dataSourceClient2 = dataKitAPI.register(dataSourceBuilder2);
                            DataTypeInt type = new DataTypeInt(DateTime.getDateTime(), Integer.parseInt(callType));

                            dataKitAPI.insert(dataSourceClient2, type);
                            Log.i(TAG, "CALL type datakit");

                            DataSourceBuilder dataSourceBuilder3 = new DataSourceBuilder().setType(DataSourceType.CU_CALL_DURATION);
                            HashMap<String, String> dataDescriptor=new HashMap<>();
                            dataDescriptor.put(METADATA.UNIT,"second");
                            dataDescriptor.put(METADATA.NAME,"Call duration");
                            dataDescriptor.put(METADATA.DESCRIPTION,"Call duration in seconds");
                            dataDescriptor.put(METADATA.FREQUENCY ,"ON_CHANGE");
                            dataDescriptor.put(METADATA.NAME,"Call Duration");
                            dataDescriptor.put(METADATA.DATA_TYPE,"int");
                            ArrayList<HashMap<String, String>> dss=new ArrayList<>();
                            dss.add(dataDescriptor);

                            dataSourceBuilder3 = dataSourceBuilder3
                                    .setMetadata(METADATA.FREQUENCY ,"ON_CHANGE")
                                    .setMetadata(METADATA.DESCRIPTION,"Call duration in seconds")
                                    .setMetadata(METADATA.NAME,"Call Duration")
                                    .setMetadata(METADATA.UNIT, "second")
                                    .setMetadata(METADATA.DATA_TYPE,"org.md2k.datakitapi.datatype.DataTypeInt");
                            dataSourceBuilder3 = dataSourceBuilder3.setDataDescriptors(dss);

                            DataSourceClient dataSourceClient3 = dataKitAPI.register(dataSourceBuilder3);
                            DataTypeInt duration = new DataTypeInt(DateTime.getDateTime(), Integer.parseInt(callDuration));

                            dataKitAPI.insert(dataSourceClient3, duration);
                            Log.i(TAG, "CALL duration datakit");

                        } catch (DataKitException e) {
                            e.printStackTrace();
                            Log.i(TAG, "CALL datakit Error");
                        }
                    }
                });
            } catch (DataKitException e) {
                e.printStackTrace();
            }

        }
        managedCursor.close();
        //System.out.println(sb);
    }
    //Deals with actual events
    private class PhonecallStartEndDetector extends PhoneStateListener {
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhonecallStartEndDetector() {}

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number){
            savedNumber = number;
        }

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = incomingNumber;
                    //onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                    //    onOutgoingCallStarted(savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        onMissedCall(savedNumber, callStartTime);
                    }
                    else if(isIncoming){
                        onIncomingCallEnded(savedNumber, callStartTime, new Date());
                    }
                    else{
                        onOutgoingCallEnded(savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;
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


}