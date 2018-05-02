package com.fc.myalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private Switch aSwitch;
    private EditText edtTime;
    private Button btnCancel;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aSwitch = findViewById(R.id.switchAlarm);
        btnCancel = findViewById(R.id.btnCancel);
        edtTime = findViewById(R.id.edtHour);

        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        timePickerDialog = new TimePickerDialog(this,this,hour,min,false);


        boolean status = getSharedPreferences(getPackageName(),MODE_PRIVATE).getBoolean("status",false);
        aSwitch.setChecked(status);
        String lastTime = getSharedPreferences(getPackageName(),MODE_PRIVATE).getString("time","");
        edtTime.setText(lastTime);

        if (status){
         btnCancel.setVisibility(View.VISIBLE);
        }else {
            btnCancel.setVisibility(View.INVISIBLE);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    timePickerDialog.show();
                }else {
                    if (pendingIntent !=null){
                        alarmManager.cancel(pendingIntent);
                        edtTime.setText("");

                        getSharedPreferences(getPackageName(),MODE_PRIVATE).edit()
                                .putString("time","")
                                .putBoolean("status",false).apply();
                        btnCancel.setVisibility(View.INVISIBLE);

                    }

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (hourOfDay>12){
            edtTime.setText(String.valueOf(hourOfDay-12)+" : "+minute+" PM ");
        }else {
            edtTime.setText(hourOfDay+" : "+minute+" AM");
        }

        setAlarm(hourOfDay,minute);
        getSharedPreferences(getPackageName(),MODE_PRIVATE).edit()
                .putString("time",edtTime.getText().toString())
                .putBoolean("status",true).apply();
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void cancelAlarm(){
        Intent intent = new Intent(this,MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);

        Intent intent = new Intent(this,MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
      //  alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);

    }

}
