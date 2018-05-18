package com.fc.myalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.fc.myalarm.DBConstants.CREATE_TABLE_PASSWORD;
import static com.fc.myalarm.DBConstants.DOMAIN;
import static com.fc.myalarm.DBConstants.PASSWORD;
import static com.fc.myalarm.DBConstants.PASSWORD_ID;
import static com.fc.myalarm.DBConstants.TABLE_PASSWORDS;
import static com.fc.myalarm.DBConstants.USERNAME;

public class PasswordDBHelper extends SQLiteOpenHelper{

    private SQLiteDatabase db;

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
        contentValues.put(USERNAME,username);
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
                String username = cursor.getString(cursor.getColumnIndex(USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(PASSWORD));

                MyPassword m = new MyPassword(domain,username,password,id,false);

                myPasswords.add(m);

            }while (cursor.moveToNext());

        }

        return myPasswords;
    }

    public int deleteEntry(int id){
        db = this.getWritableDatabase();

        String whereClause = PASSWORD_ID+"=?";
        String[] whereArgs = {String.valueOf(id)};

        return db.delete(TABLE_PASSWORDS,whereClause,whereArgs);
    }

}
