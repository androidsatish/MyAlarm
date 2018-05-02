package com.fc.myalarm;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    Uri ring;
    MediaPlayer mediaPlayer;
    private TextView txtMessage;
    private FloatingActionButton btnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        txtMessage = findViewById(R.id.txtMessage);
        btnStop = findViewById(R.id.btnStop);


        txtMessage.setText("Alarm Started.... \nWake Up !!!");

        ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) != null){
            ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        else {
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this,ring);

        if (mediaPlayer != null){
            mediaPlayer.start();

            Log.d("#######","Media player is on "+ "and ring  "+ring);
        }else {
            Log.d("#######","Media player is null "+ "and ring  "+ring);
        }

        btnStop.setVisibility(View.VISIBLE);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnStop.setVisibility(View.INVISIBLE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                finish();

            }
        });

    }

    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            super.finishAndRemoveTask();
        }else {
            super.finish();
        }

    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
    }


}
