package com.fc.myalarm;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPassword implements Parcelable{
    private String domain,username,password;
    private int id;
    private boolean isVisible;

    public MyPassword(String domain, String username, String password, int id, boolean isVisible) {
        this.domain = domain;
        this.username = username;
        this.password = password;
        this.id = id;
        this.isVisible = isVisible;
    }

    protected MyPassword(Parcel in) {
        domain = in.readString();
        username = in.readString();
        password = in.readString();
        id = in.readInt();
        isVisible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(domain);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(id);
        dest.writeByte((byte) (isVisible ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyPassword> CREATOR = new Creator<MyPassword>() {
        @Override
        public MyPassword createFromParcel(Parcel in) {
            return new MyPassword(in);
        }

        @Override
        public MyPassword[] newArray(int size) {
            return new MyPassword[size];
        }
    };

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
