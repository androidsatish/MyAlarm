package com.fc.myalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final int alarmId = intent.getIntExtra("id",0);

      //  Toast.makeText(context,"In Receiver",Toast.LENGTH_SHORT).show();
        try {
            context.startActivity(new Intent(context,AlarmActivity.class).putExtra("id",alarmId)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }catch (Exception e){
            e.printStackTrace();
        }
    //    Toast.makeText(context,"Activity Intent fired",Toast.LENGTH_SHORT).show();

    }
}
