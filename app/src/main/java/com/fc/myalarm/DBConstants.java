package com.fc.myalarm;

public class DBConstants {
    public static final String TABLE_PASSWORDS = "Passwards_123";
    public static final String DOMAIN = "domain_name4";
    public static final String USERNAME = "user_name4";
    public static final String PASSWORD = "password4";
    public static final String PASSWORD_ID = "password_id4";

    public static final String CREATE_TABLE_PASSWORD = "create table if not exists "+TABLE_PASSWORDS+
            "("+PASSWORD_ID+" integer primary key autoincrement,"+
            DOMAIN+" text,"+
            USERNAME+" text,"+
            PASSWORD+" text)";
}
