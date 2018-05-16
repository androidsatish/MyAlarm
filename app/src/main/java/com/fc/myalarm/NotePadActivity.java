package com.fc.myalarm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NotePadActivity extends AppCompatActivity {

    private LinedEditText editor;
    private ImageView imgDelete,imgSave;
    final static String fileName = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pad);
        init();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (!checkPermission()){
                requestPermission();
            }else {
                new ReadFile().execute();
            }
        }else {
            new ReadFile().execute();
        }
    }

    private boolean checkPermission() {
         return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1144);
    }

    private void init() {
        imgDelete = findViewById(R.id.imgDelete);
        imgSave = findViewById(R.id.imgSave);
        editor = findViewById(R.id.edtTextEditor);

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()){
                    new WriteFile().execute(editor.getText().toString());
                }
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileHelper.deleteFile(fileName);
                new ReadFile().execute();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1144){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                new ReadFile().execute();
            }
        }
    }

    public class WriteFile extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            String data = strings[0];

            boolean saved = FileHelper.saveToFile(data,fileName,false);

            return saved;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class ReadFile extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

           String fileData =  FileHelper.ReadFile(getApplicationContext(),fileName);

            return fileData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            editor.setText(s);
            editor.setSelection(editor.getText().toString().length());
        }
    }
}
