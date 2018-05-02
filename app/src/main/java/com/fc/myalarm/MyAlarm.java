package com.fc.myalarm;

public class MyAlarm {
    public long ID;
    public int HOUR,MIN;
    public String LABEL,RING,RING_TITLE;
    public boolean status;

    public MyAlarm(long ID, int HOUR, int MIN, String LABEL, String RING, String RING_TITLE, boolean status) {
        this.ID = ID;
        this.HOUR = HOUR;
        this.MIN = MIN;
        this.LABEL = LABEL;
        this.RING = RING;
        this.RING_TITLE = RING_TITLE;
        this.status = status;
    }

    public MyAlarm(long ID, int HOUR, int MIN, boolean status) {
        this.ID = ID;
        this.HOUR = HOUR;
        this.MIN = MIN;
        this.status = status;
    }

    public MyAlarm(int HOUR, int MIN, boolean status) {
        this.HOUR = HOUR;
        this.MIN = MIN;
        this.status = status;
    }

    public String getRING_TITLE() {
        return RING_TITLE;
    }

    public void setRING_TITLE(String RING_TITLE) {
        this.RING_TITLE = RING_TITLE;
    }

    public String getRING() {
        return RING;
    }

    public void setRING(String RING) {
        this.RING = RING;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public int getHOUR() {
        return HOUR;
    }

    public void setHOUR(int HOUR) {
        this.HOUR = HOUR;
    }

    public int getMIN() {
        return MIN;
    }

    public void setMIN(int MIN) {
        this.MIN = MIN;
    }


    public String getLABEL() {
        return LABEL;
    }

    public void setLABEL(String LABEL) {
        this.LABEL = LABEL;
    }
}
