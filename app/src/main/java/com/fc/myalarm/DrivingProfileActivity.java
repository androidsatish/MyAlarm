package com.fc.myalarm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class DrivingProfileActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    public static final int ID = 11111;
    private EditText edtMessage;
    private Button btnSave;
    private Switch aSwitchMessage,aSwitchRejectCall;
    private SharedPreferences preferences;
    public static final String IS_MSG = "is_message_service_enabled";
    public static final String IS_REJECT = "is_reject_call_enabled";
    public static final String MSG = "message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_profile);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
        init();
    }

    private void init() {
        edtMessage = findViewById(R.id.edtMessage);
        btnSave = findViewById(R.id.btnSave);
        aSwitchRejectCall = findViewById(R.id.aSwitchReject);
        aSwitchMessage = findViewById(R.id.aSwitchMessage);

        aSwitchRejectCall.setOnCheckedChangeListener(this);
        aSwitchMessage.setOnCheckedChangeListener(this);

        preferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);

        aSwitchMessage.setChecked(preferences.getBoolean(IS_MSG,false));
        aSwitchRejectCall.setChecked(preferences.getBoolean(IS_REJECT,false));
        String msg = preferences.getString(MSG,"");
        if (msg.equals("")){
            edtMessage.setHint("Message To Send");
        }else {
            edtMessage.setText(msg);
        }

        btnSave.setOnClickListener(v -> {
            if (checkPermission()){
                boolean isMessage = aSwitchMessage.isChecked();
                boolean isReject = aSwitchRejectCall.isChecked();
                String msg1 = edtMessage.getText().toString();

                preferences.edit()
                        .putString(MSG, msg1)
                        .putBoolean(IS_MSG,isMessage)
                        .putBoolean(IS_REJECT,isReject)
                        .apply();
            }else {
                requestPermission();
            }

        });

    }
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS},1145);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.aSwitchMessage:
                preferences.edit()
                    .putBoolean(IS_MSG,isChecked)
                    .commit();

                if (isChecked){
                    edtMessage.requestFocus();
                    createNotification();
                }else {
                    cancelNotification();
                }

                break;
            case R.id.aSwitchReject:
                preferences.edit()
                        .putBoolean(IS_REJECT,isChecked)
                        .commit();
                break;

        }
    }

    private void cancelNotification() {
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(ID);
    }

    private void createNotification() {
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        String channelId = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1212","Basic",NotificationManager.IMPORTANCE_DEFAULT);

            mManager.createNotificationChannel(channel);
            channelId = channel.getId();
        }

        Intent intent = new Intent(this,DrivingProfileActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,114,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this,channelId);
        mBuilder.setSmallIcon(R.drawable.ic_bike)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_car))
                .setContentTitle("Driving Mode ON")
                .setContentText("Reject Call and send SMS is active as Driving Mode is On")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false);

        mManager.notify(ID,mBuilder.build());


    }
}
