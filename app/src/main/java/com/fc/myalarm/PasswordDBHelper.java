package com.fc.myalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class PasswordDBHelper extends SQLiteOpenHelper{

    private SQLiteDatabase db;

    public static final String TABLE_PASSWORDS = "Passwards";
    public static final String DOMAIN = "domain_name";
    public static final String USERANME = "user_name";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_ID = "password_id";

    public static final String CREATE_TABLE_PASSWORD = "create table if not exists "+TABLE_PASSWORDS+
            "("+PASSWORD_ID+" integer primary key autoincrement,"+
            DOMAIN+" text,"+
            USERANME+" text,"+
            PASSWORD+" text)";

    public PasswordDBHelper(Context context,String path) {
        super(context,path+"myPassword.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("#######","Database created @ "+db.getPath());
        db.execSQL(CREATE_TABLE_PASSWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addEntry(String domain,String username,String password){
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DOMAIN,domain);
        contentValues.put(USERANME,username);
        contentValues.put(PASSWORD,password);

       return db.insert(TABLE_PASSWORDS,null,contentValues);
    }

    public ArrayList<MyPassword> getPasswords(){
        ArrayList<MyPassword>myPasswords= new ArrayList<>();

        db = this.getReadableDatabase();

      Cursor cursor =  db.query(TABLE_PASSWORDS,null,null,null,null,null,null);
        if (cursor.moveToFirst()){

            do {

                int id = cursor.getInt(cursor.getColumnIndex(PASSWORD_ID));
                String domain = cursor.getString(cursor.getColumnIndex(DOMAIN));
                String username = cursor.getString(cursor.getColumnIndex(USERANME));
                String password = cursor.getString(cursor.getColumnIndex(PASSWORD));

                MyPassword m = new MyPassword(domain,username,password,id);

                myPasswords.add(m);

            }while (cursor.moveToNext());

        }

        return myPasswords;
    }


}
