package com.fc.myalarm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterAlarmList extends RecyclerView.Adapter<AdapterAlarmList.AlarmViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<MyAlarm>myAlarmArrayList;
    private OnAlarmChangedListener listener;

    public AdapterAlarmList(Context context,OnAlarmChangedListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<MyAlarm> getMyAlarmArrayList() {
        return myAlarmArrayList;
    }

    public void setMyAlarmArrayList(ArrayList<MyAlarm> myAlarmArrayList) {
        this.myAlarmArrayList = myAlarmArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_adapter_alarm_list,parent,false);
        AlarmViewHolder viewHolder = new AlarmViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {


        holder.txtTime.setText(getTimeString(myAlarmArrayList.get(position).getHOUR(),myAlarmArrayList.get(position).getMIN()));
        holder.edtDescription.setText(myAlarmArrayList.get(position).getLABEL());
        holder.switchAlarm.setChecked(myAlarmArrayList.get(position).isStatus());

        holder.txtRing.setText(myAlarmArrayList.get(position).getRING_TITLE());
    }

    private String getTimeString(int hour, int min) {
    String minute;
        if (min<10){
            minute = "0"+min;
        }else {
            minute = String.valueOf(min);
        }

        if (hour>12){
            return String.valueOf(hour-12)+" : "+minute+" PM ";
        }else {
            return hour+" : "+minute+" AM";
        }

    }



    @Override
    public int getItemCount() {
        if (myAlarmArrayList !=null){
            return myAlarmArrayList.size();
        }else return 0;

    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTime,txtRing;
        public Switch switchAlarm;
        public EditText edtDescription,editText;
        public ImageView imgDelete;
        public AlarmViewHolder(View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtAlarmTime);
            switchAlarm = itemView.findViewById(R.id.switchAlarm);
            txtRing = itemView.findViewById(R.id.txtRing);
            edtDescription = itemView.findViewById(R.id.edtAlarmDescription);
            imgDelete = itemView.findViewById(R.id.imgDelete);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int id = (int) myAlarmArrayList.get(getAdapterPosition()).getID();

                    listener.onAlarmSelected(getAdapterPosition(),id);
                }
            });

            txtRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int id = (int) myAlarmArrayList.get(getAdapterPosition()).getID();

                    listener.onAlarmRingChanged(getAdapterPosition(),id);
                }
            });

            switchAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int id = (int) myAlarmArrayList.get(getAdapterPosition()).getID();

                    if (myAlarmArrayList.get(getAdapterPosition()).isStatus()){
                        listener.onAlarmStatusChanged(getAdapterPosition(),id,false);
                        switchAlarm.setChecked(false);
                    }else {
                        listener.onAlarmStatusChanged(getAdapterPosition(),id,true);
                        switchAlarm.setChecked(true);
                    }
                }
            });

          txtTime.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  final int id = (int) myAlarmArrayList.get(getAdapterPosition()).getID();
                 listener.onAlarmTimeChanged(getAdapterPosition(),id);

              }
          });

            edtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){

                        editText = new EditText(context);
                        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        editText.setText(myAlarmArrayList.get(getAdapterPosition()).getLABEL());

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Enter Label for Alarm");
                        builder.setView(editText);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onAlarmLabelChanged(getAdapterPosition(), (int) myAlarmArrayList.get(getAdapterPosition()).getID(),editText.getText().toString());
                            }
                        });


                        AlertDialog dialog = builder.create();

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        edtDescription.clearFocus();

                    }
                }
            });
        }
    }
}
