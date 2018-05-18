package com.fc.myalarm;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class PasswordManagerActivity extends AppCompatActivity {

    private static final String TAG = "#####";
    private static final int REQUEST_CODE_CREATE_FILE = 11111;
    private static final int REQUEST_CODE_OPEN_ITEM = 01010;
    private static final int INTENT_AUTHENTICATE = 22554;
    private boolean isAuthenticated = false;
    private LinearLayout mRootLayout;
    private FloatingActionButton btnSave;
    private Snackbar sBar;
    private Button btnGetSavedPasswords;
    private EditText edtDomain,edtUsername,edtPassword;
    private PasswordDBHelper passwordDBHelper;

    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myAlarm/" ;
    public static final String DATABASE_FILE_NAME = "myPassword.db";

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private int RC_SIGN_IN = 1454;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        if (account == null){
            Log.i(TAG, "Account Null");
            setUpGoogleAccount();
        }else if (account.isExpired()){
            Log.i(TAG, "Account expired");
            setUpGoogleAccount();

        }else {
            Log.i(TAG, "Account "+account.getDisplayName());
            getDriveResources(account);
        }
    }

    private void setUpGoogleAccount() {
        mGoogleSignInClient = buildGoogleSignInClient();
        signIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (!checkPermission()){
                requestPermission();
            }else {
                createDirectory();
            }
        }else {
            createDirectory();
        }


        init();
        passwordDBHelper = new PasswordDBHelper(this,path);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1144);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1144){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createDirectory();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            updateViewWithGoogleSignInAccountTask(task);
        }else if (requestCode == REQUEST_CODE_CREATE_FILE){


        }else if (requestCode == REQUEST_CODE_OPEN_ITEM){
            if (resultCode == RESULT_OK) {
                DriveId driveId = data.getParcelableExtra(
                        OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                mOpenItemTaskSource.setResult(driveId);
            } else {
                mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
            }
        }else if (requestCode == INTENT_AUTHENTICATE){
            if (resultCode == RESULT_OK){
                getAllPasswords();
                isAuthenticated = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isAuthenticated = false;
                    }
                },1000*60);
            }else {
                showMessage("Auth Failed !");
            }
        }
    }

    private void updateViewWithGoogleSignInAccountTask(Task<GoogleSignInAccount> task){
        Log.i(TAG, "Update view with sign in account task");

        task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                Log.i(TAG, "Sign in success  "+googleSignInAccount.getDisplayName());

                getDriveResources(googleSignInAccount);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Sign in failed", e);
            }
        });
    }

    private void getDriveResources(GoogleSignInAccount googleSignInAccount) {

        mDriveClient = Drive.getDriveClient(getApplicationContext(),googleSignInAccount);

        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(),googleSignInAccount);

    }

    private void createFileToUpload(DriveResourceClient mDriveResourceClient, final DriveClient mDriveClient) {

        Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

        createContentsTask.continueWithTask(task ->{

            DriveContents contents = task.getResult();
            OutputStream outputStream = contents.getOutputStream();

            File dbFile = new File(path,DATABASE_FILE_NAME);

            try (Writer writer = new OutputStreamWriter(outputStream)) {
                //  writer.write("Hello World! ..... from Satish");

                try (InputStream in = new FileInputStream(dbFile)){
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        outputStream.write(buf,0,len);
                    }
                }

            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(dbFile.getName()+"_"+getCurrentDate())
                    .setMimeType("text/plain")
                    .setStarred(true)
                    .build();

            CreateFileActivityOptions createOptions =
                    new CreateFileActivityOptions.Builder()
                            .setInitialDriveContents(contents)
                            .setInitialMetadata(changeSet)
                            .build();

            return mDriveClient.newCreateFileActivityIntentSender(createOptions);

        }).addOnSuccessListener(this,intentSender ->{
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_CREATE_FILE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to create file", e);
                finish();

            }
        }).addOnFailureListener(this,e ->{
            Log.e(TAG, "Unable to create file", e);
            finish();
        });
    }

    private void createDBFileUpload(DriveResourceClient mDriveResourceClient,DriveClient mDriveClient){


        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();

                    File dbFile = new File(path,DATABASE_FILE_NAME);

                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                      //  writer.write("Hello World! ..... from Satish");

                       try (InputStream in = new FileInputStream(dbFile)){
                           byte[] buf = new byte[1024];
                           int len;
                           while ((len = in.read(buf)) > 0) {
                               outputStream.write(buf,0,len);
                           }
                       }

                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(dbFile.getName()+"_"+getCurrentDate())
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            showMessage(getString(R.string.file_created)+" : "+driveFile.getDriveId().encodeToString());
                            Log.d("#########","Drive id "+driveFile.getDriveId().encodeToString());
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });

    }

    private void showMessage(String string) {
        Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
    }

    private void getDbFile(){
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    finish();
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    // Process contents...
                    // [START_EXCLUDE]
                    // [START read_as_string]

                    File dbFile = new File(path,DATABASE_FILE_NAME);

                    if (!dbFile.exists()){
                        dbFile.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(dbFile);

                    try (InputStream in = contents.getInputStream()){
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            fileOutputStream.write(buf,0,len);
                        }

                        showMessage(getString(R.string.content_loaded));
                    }

                    // [END read_as_string]
                    // [END_EXCLUDE]
                    // [START discard_contents]
                    Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                    // [END discard_contents]
                    return discardTask;
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    // [START_EXCLUDE]
                    Log.e(TAG, "Unable to read contents", e);
                    showMessage(getString(R.string.read_failed));
                    finish();
                    // [END_EXCLUDE]
                });
        // [END read_contents]
    }

    protected Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle(DATABASE_FILE_NAME)
                        .build();
        return pickItem(openOptions);
    }
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Password Manager");

        btnSave.setOnClickListener(v -> addPasswordEntry());

        btnGetSavedPasswords.setOnClickListener(v ->
                {
                    if (!isAuthenticated){
                        authenticateUser();
                    }else {
                        getAllPasswords();
                    }
                });

    }

    private void authenticateUser(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (km.isKeyguardSecure()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent("Authenticate", "Tell me is that you ?");
                startActivityForResult(authIntent, INTENT_AUTHENTICATE);
            }
        }
    }

    private void getAllPasswords() {
        
        ArrayList<MyPassword> myPasswords = passwordDBHelper.getPasswords();
        Log.d("#######","Password list  "+myPasswords.size());

        if (myPasswords != null && myPasswords.size()>0){
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data",myPasswords);

            PasswordSheetFragment fragment = new PasswordSheetFragment();
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(),"List");
        }else {
            showMessage("No Password saved");
        }


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

    private String getCurrentDate(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("######","Date "+sfd.format(date));
        return sfd.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,R.string.syncDown);
        menu.add(0,2,0,R.string.sync);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:getDbFile();
                break;
            case 2:if (passwordDBHelper.getPasswords() != null && passwordDBHelper.getPasswords().size()>0){
               // createDBFileUpload(mDriveResourceClient,mDriveClient);
                createFileToUpload(mDriveResourceClient,mDriveClient);
            }else showMessage("Empty Database cannot upload");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
