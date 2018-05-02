package com.fc.myalarm;

public interface OnAlarmChangedListener {
    void onAlarmStatusChanged(int index,int alarmId,boolean status);
    void onAlarmTimeChanged(int index,int alarmId);
    void onAlarmLabelChanged(int index,int alarmId,String label);
    void onAlarmSelected(int index,int alarmId);
    void onAlarmRingChanged(int index,int alarmId);
}
