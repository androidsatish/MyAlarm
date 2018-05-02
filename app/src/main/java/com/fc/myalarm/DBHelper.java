package com.fc.myalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    private SQLiteDatabase db;

    public static final String TABLE_ALARM = "Alarms";
    public static final String ALARM_ID = "alarm_id";
    public static final String ALARM_LABEL = "alarm_label";
    public static final String ALARM_HOUR = "alarm_hour";
    public static final String ALARM_MIN = "alarm_min";
    public static final String ALARM_STATUS = "alarm_status";
    public static final String ALARM_RING = "alarm_ring";
    public static final String ALARM_RING_TITLE = "alarm_ring_title";

    public static final String CREATE_TABLE_ALARMS = "create table if not exists "+TABLE_ALARM+
            "("+ALARM_ID+" integer primary key autoincrement,"+
            ALARM_LABEL+" text,"+
            ALARM_HOUR+" integer,"+
            ALARM_MIN+" integer,"+
            ALARM_STATUS+" integer,"+
            ALARM_RING+" text,"+
            ALARM_RING_TITLE+" text)";

    public DBHelper(Context context) {
        super(context,"myalarm.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALARMS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<MyAlarm> getAlarms(){
        db = this.getReadableDatabase();
        ArrayList<MyAlarm> myAlarmArrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_ALARM,null,null,null,null,null,null);

        if (cursor.moveToFirst()){

           do {

              int id = cursor.getInt(cursor.getColumnIndex(ALARM_ID));
               int hour = cursor.getInt(cursor.getColumnIndex(ALARM_HOUR));
               int min = cursor.getInt(cursor.getColumnIndex(ALARM_MIN));
               int status = cursor.getInt(cursor.getColumnIndex(ALARM_STATUS));
               String label = cursor.getString(cursor.getColumnIndex(ALARM_LABEL));
               String ring = cursor.getString(cursor.getColumnIndex(ALARM_RING));
               String ring_title = cursor.getString(cursor.getColumnIndex(ALARM_RING_TITLE));

               Log.d("#####","status: "+status);
               MyAlarm myAlarm = new MyAlarm(id,hour,min,label,ring,ring_title,status >0 ? true:false);

               myAlarmArrayList.add(myAlarm);

           }while (cursor.moveToNext());

        }


        return myAlarmArrayList;
    }

    public MyAlarm getAlarm(int alarmId){
        db = this.getReadableDatabase();

        MyAlarm myAlarm = null;

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(alarmId)};

        Cursor cursor = db.query(TABLE_ALARM,null,whereClause,whereArgs,null,null,null);

        if (cursor.moveToFirst()){

                int id = cursor.getInt(cursor.getColumnIndex(ALARM_ID));
                int hour = cursor.getInt(cursor.getColumnIndex(ALARM_HOUR));
                int min = cursor.getInt(cursor.getColumnIndex(ALARM_MIN));
                int status = cursor.getInt(cursor.getColumnIndex(ALARM_STATUS));
                String label = cursor.getString(cursor.getColumnIndex(ALARM_LABEL));
                String ring = cursor.getString(cursor.getColumnIndex(ALARM_RING));
            String ring_title = cursor.getString(cursor.getColumnIndex(ALARM_RING_TITLE));

                Log.d("#####","status: "+status);
                 myAlarm = new MyAlarm(id,hour,min,label,ring,ring_title ,status >0 ? true:false);

        }


        return myAlarm;
    }

    public long addAlarm(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ALARM_LABEL,myAlarm.getLABEL());
        contentValues.put(ALARM_HOUR,myAlarm.getHOUR());
        contentValues.put(ALARM_MIN,myAlarm.getMIN());
        contentValues.put(ALARM_STATUS,myAlarm.isStatus());
        contentValues.put(ALARM_RING,myAlarm.getRING());


        Log.d("#####","status: "+myAlarm.isStatus());

     return db.insert(TABLE_ALARM,null,contentValues);

    }

    public int changeTime(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(myAlarm.getID())};

        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_HOUR,myAlarm.getHOUR());
        contentValues.put(ALARM_MIN,myAlarm.getMIN());

        return db.update(TABLE_ALARM,contentValues,whereClause,whereArgs);

    }

    public int changeLabel(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(myAlarm.getID())};

        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_LABEL,myAlarm.getLABEL());

        return db.update(TABLE_ALARM,contentValues,whereClause,whereArgs);

    }

    public int changeStatus(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(myAlarm.getID())};

        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_STATUS,myAlarm.isStatus());

        return db.update(TABLE_ALARM,contentValues,whereClause,whereArgs);

    }
    public int changeRing(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(myAlarm.getID())};

        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_RING,myAlarm.getRING());
        contentValues.put(ALARM_RING_TITLE,myAlarm.getRING_TITLE());


        return db.update(TABLE_ALARM,contentValues,whereClause,whereArgs);

    }
    public int deleteAlarm(MyAlarm myAlarm){
        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(myAlarm.getID())};

        return db.delete(TABLE_ALARM,whereClause,whereArgs);

    }
    public String getLable(int alarmId){
        db = this.getReadableDatabase();

        db = this.getWritableDatabase();

        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(alarmId)};

       Cursor cursor = db.query(TABLE_ALARM,new String[]{ALARM_LABEL},whereClause,whereArgs,null,null,null);

       if (cursor.moveToFirst()){
           return cursor.getString(cursor.getColumnIndex(ALARM_LABEL));
       }else return "Alarm Started.... \nWake Up !!!";

    }

    public String getRing(int alarmId){
        db = this.getReadableDatabase();



        String whereClause = ALARM_ID+"= ?";
        String[] whereArgs = {String.valueOf(alarmId)};

        Cursor cursor = db.query(TABLE_ALARM,new String[]{ALARM_RING},whereClause,whereArgs,null,null,null);

        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(ALARM_RING));
        }else return "";

    }
}
