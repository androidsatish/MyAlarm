package com.fc.myalarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterPasswordList extends RecyclerView.Adapter<AdapterPasswordList.DataViewHolder>{

    private Context context;
    private ArrayList<MyPassword>myPasswordArrayList;
    private LayoutInflater inflater;
    private RecycleViewItemListener listener;

    public AdapterPasswordList(Context context,RecycleViewItemListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<MyPassword> getMyPasswordArrayList() {
        return myPasswordArrayList;
    }

    public void setMyPasswordArrayList(ArrayList<MyPassword> myPasswordArrayList) {
        this.myPasswordArrayList = myPasswordArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_adapter_password_list,parent,false);

        DataViewHolder viewHolder = new DataViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.txtUsername.setText(myPasswordArrayList.get(position).getUsername());
        holder.txtPassword.setText(myPasswordArrayList.get(position).getPassword());
        holder.txtDomain.setText(myPasswordArrayList.get(position).getDomain());
    }

    @Override
    public int getItemCount() {
        return myPasswordArrayList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{
        TextView txtDomain,txtUsername,txtPassword;
        ImageView btnDelete,btnEdit;
        public DataViewHolder(View itemView) {
            super(itemView);
            txtDomain = itemView.findViewById(R.id.txtDomainName);
            txtPassword = itemView.findViewById(R.id.txtPassword);
            txtUsername = itemView.findViewById(R.id.txtUserName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyPassword myPassword = myPasswordArrayList.get(getAdapterPosition());

                    listener.onEditClicked(getAdapterPosition(),myPassword);
                }
            });


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   MyPassword myPassword = myPasswordArrayList.get(getAdapterPosition());

                   listener.onDeleteClicked(getAdapterPosition(),myPassword);

                }
            });

        }
    }

    public interface RecycleViewItemListener{
        void onDeleteClicked(int index,MyPassword myPassword);
        void onEditClicked(int index,MyPassword myPassword);
    }

}
