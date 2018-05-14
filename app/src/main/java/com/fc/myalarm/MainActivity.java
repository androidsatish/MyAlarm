package com.fc.myalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,OnAlarmChangedListener{

    private static final int RQS_RINGTONEPICKER = 666;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private FloatingActionButton btnAdd;
    private RecyclerView listViewAlarms;
    private AdapterAlarmList adapterAlarmList;
    private ArrayList<MyAlarm>myAlarmArrayList = new ArrayList<>();
    private boolean isNewAlarm = true;
    private int mIndex;
    private long mAlarmId;
    private DBHelper dbHelper;
    private int currentHour,currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        btnAdd = findViewById(R.id.btnAddAlarm);
        listViewAlarms = findViewById(R.id.listAlarms);
        listViewAlarms.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listViewAlarms.addItemDecoration(itemDecoration);


        adapterAlarmList = new AdapterAlarmList(this,this);

        listViewAlarms.setAdapter(adapterAlarmList);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNewAlarm = true;
                showTimePicker();

            }
        });

        Calendar calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        myAlarmArrayList = dbHelper.getAlarms();

        if (myAlarmArrayList.size()>0){
            adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);
        }
    }

    private void showTimePicker() {
        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this,this,hour,min,false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

        if (isNewAlarm){


            MyAlarm myAlarm = new MyAlarm(hourOfDay,minute,true);

          long id =  dbHelper.addAlarm(myAlarm);

          if (id>0){

              MyAlarm alarm = new MyAlarm(id,hourOfDay,minute,true);

              myAlarmArrayList.add(alarm);

              adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);

              setAlarm((int) id,hourOfDay,minute);



              Toast.makeText(getApplicationContext(),"Alarm set for ",Toast.LENGTH_SHORT).show();
          }


        }else {

            final MyAlarm myAlarm = getObjectById(mAlarmId);

            myAlarm.setHOUR(hourOfDay);
            myAlarm.setMIN(minute);
            myAlarmArrayList.remove(mIndex);
            myAlarmArrayList.add(mIndex,myAlarm);
            adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);
            dbHelper.changeTime(myAlarm);


            updateAlarm((int) myAlarm.getID(),hourOfDay,minute);

        }


    }

    private void updateAlarm(int alarmId, int hourOfDay, int minute) {
        Intent intent = new Intent(this,MyAlarmReceiver.class);
        intent.putExtra("id",alarmId);
        pendingIntent = PendingIntent.getBroadcast(this,alarmId,intent,0);

        try {
            alarmManager.cancel(pendingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        },500);


    }

    private void cancelAlarm(int alarmId){
        Intent intent = new Intent(this,MyAlarmReceiver.class);
        intent.putExtra("id",alarmId);
        pendingIntent = PendingIntent.getBroadcast(this,alarmId,intent,0);

        try {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(getApplicationContext(),"Alarm Canceled",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setAlarm(int alarmId,int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);

        Intent intent = new Intent(this,MyAlarmReceiver.class);
        intent.putExtra("id",alarmId);
        pendingIntent = PendingIntent.getBroadcast(this,alarmId,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

       // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);

        Log.d("#####","Alarm id :"+alarmId+" set for "+hourOfDay+":"+minute);

    }

    @Override
    public void onAlarmStatusChanged(int index,int alarmId, boolean status) {
        Log.d("#####","ID: "+alarmId+" Status: "+status);
        MyAlarm myAlarm = getObjectById(alarmId);

        if (myAlarm != null){
            myAlarm.setStatus(status);
            myAlarmArrayList.remove(index);
            myAlarmArrayList.add(index,myAlarm);
            adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);
            dbHelper.changeStatus(myAlarm);

            if (status){
                setAlarm(alarmId,myAlarm.getHOUR(),myAlarm.getMIN());
            }else {
                cancelAlarm(alarmId);
            }


        }
    }

    private MyAlarm getObjectById(long alarmId) {

        MyAlarm alarm = null;

        for (int i=0;i<myAlarmArrayList.size();i++){
            if (myAlarmArrayList.get(i).getID() == alarmId){
                return myAlarmArrayList.get(i);
            }
        }

        return alarm;

    }

    @Override
    public void onAlarmTimeChanged(int index,int alarmId) {
        Log.d("#####","ID: "+alarmId);
        MyAlarm myAlarm = getObjectById(alarmId);

        if (myAlarm !=null){
            isNewAlarm = false;
            mAlarmId = myAlarm.getID();
            mIndex = index;

        TimePickerDialog tpd = new TimePickerDialog(this,this,myAlarm.getHOUR(),myAlarm.getMIN(),false);
            tpd.show();
        }

    }

    @Override
    public void onAlarmLabelChanged(int index,int alarmId, String label) {
        Log.d("#####","ID: "+alarmId+" Label: "+label);
        MyAlarm myAlarm = getObjectById(alarmId);

        if (myAlarm != null){
            myAlarm.setLABEL(label);
            myAlarmArrayList.remove(index);
            myAlarmArrayList.add(index,myAlarm);
            adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);
            dbHelper.changeLabel(myAlarm);
        }
    }

    @Override
    public void onAlarmSelected(final int index, final int alarmId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MyAlarm myAlarm = dbHelper.getAlarm(alarmId);

                dbHelper.deleteAlarm(myAlarm);

                myAlarmArrayList.remove(index);

                adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);

                cancelAlarm(alarmId);

                Toast.makeText(getApplicationContext(),"Alarm Removed",Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle("Do you want to delete this alarm ?");

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        alertDialog.show();

    }

    @Override
    public void onAlarmRingChanged(int index, int alarmId) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, RQS_RINGTONEPICKER);


        MyAlarm myAlarm = getObjectById(alarmId);

        if (myAlarm !=null){
            isNewAlarm = false;
            mAlarmId = myAlarm.getID();
            mIndex = index;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RQS_RINGTONEPICKER) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            Log.d("#####","Selected Uri "+uri);

            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(),uri);

            String title = ringtone.getTitle(getApplicationContext());

            Log.d("#####","Ringtone Title : "+ringtone.getTitle(getApplicationContext()));


            MyAlarm myAlarm = getObjectById(mAlarmId);

            if (myAlarm != null){
                myAlarm.setRING(uri.toString());
                myAlarm.setRING_TITLE(title);
                myAlarmArrayList.remove(mIndex);
                myAlarmArrayList.add(mIndex,myAlarm);

                adapterAlarmList.setMyAlarmArrayList(myAlarmArrayList);
                dbHelper.changeRing(myAlarm);
            }

        }
    }
}
