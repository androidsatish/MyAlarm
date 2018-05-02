package com.fc.myalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startActivity(new Intent(context,AlarmActivity.class));

    }
}
