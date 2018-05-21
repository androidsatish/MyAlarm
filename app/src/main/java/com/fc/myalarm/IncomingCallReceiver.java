package com.fc.myalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver{
    private SharedPreferences preferences;
    private boolean isReject,isMessage;
    private String msg;

    private static String mLastState;
    @Override
    public void onReceive(Context context, Intent intent) {


        preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        isMessage = preferences.getBoolean(DrivingProfileActivity.IS_MSG,false);
        isReject = preferences.getBoolean(DrivingProfileActivity.IS_REJECT,false);
        msg = preferences.getString(DrivingProfileActivity.MSG," I am driving now, call u later");
        try {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);


            if (!state.equals(mLastState)){
                mLastState = state;

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.d("#######","Incoming Call Received from : "+number);
                    checkNumber(context,number);


                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkNumber(Context context, String number) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = context.getContentResolver().query(uri, projection,null, null, null);

            if (people.moveToFirst()){
                do {
                    int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name = people.getString(indexName);
                    String mobile= people.getString(indexNumber);

                    Log.d("#######","Name : "+name+" Number : "+mobile.trim().replace("-","").replace(" ",""));

                    if (number.equals(mobile.trim().replace("-","").replace(" ",""))){

                        Log.d("#######","Name Found is  : "+name);
                        showMsg(context,"Name is Found :"+name);
                        rejectCallAndSendMsg(context,name,number);
                        break;
                    }

                }while (people.moveToNext());
            }

    }

    private void rejectCallAndSendMsg(Context context, String name, String number) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (isReject){
            rejectCall(telephonyManager);
        }

        if (isMessage){
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(number,"",msg,null,null);
        }

    }
    private void rejectCall(TelephonyManager telephonyManager){

        try {

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void showMsg(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
