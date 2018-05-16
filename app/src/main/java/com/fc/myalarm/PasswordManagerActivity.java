package com.fc.myalarm;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PasswordManagerActivity extends AppCompatActivity {

    private LinearLayout mRootLayout;
    private FloatingActionButton btnSave;
    private Snackbar sBar;
    private Button btnGetSavedPasswords;
    private EditText edtDomain,edtUsername,edtPassword;
    private PasswordDBHelper passwordDBHelper;

    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myAlarm/" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);
        createDirectory();
        init();
        passwordDBHelper = new PasswordDBHelper(this,path);
    }

    private void createDirectory() {
        File file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }
    }

    private void init() {
        btnSave = findViewById(R.id.btnSave);
        btnGetSavedPasswords = findViewById(R.id.btnGetPasswords);
        edtDomain = findViewById(R.id.edtDomain);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUserName);
        mRootLayout = findViewById(R.id.rootLayout);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPasswordEntry();
            }
        });

        btnGetSavedPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllPasswords();
            }
        });
    }

    private void getAllPasswords() {
        ArrayList<MyPassword> myPasswords = passwordDBHelper.getPasswords();
        Log.d("#######","Password list  "+myPasswords.size());

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data",myPasswords);

        PasswordSheetFragment fragment = new PasswordSheetFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),"List");
    }

    private void addPasswordEntry() {
        String domain = edtDomain.getText().toString();
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        long id = passwordDBHelper.addEntry(domain,username,password);

        if (id>0){
            clearInputs();
            sBar = Snackbar.make(mRootLayout,"Password Saved Successfully !", BaseTransientBottomBar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sBar.dismiss();
                }
            });
            sBar.show();
        }
    }

    private void clearInputs() {
        edtUsername.setText("");
        edtPassword.setText("");
        edtDomain.setText("");
        edtDomain.requestFocus();
    }


}
