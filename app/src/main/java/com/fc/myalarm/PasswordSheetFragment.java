package com.fc.myalarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PasswordSheetFragment extends BottomSheetDialogFragment implements AdapterPasswordList.RecycleViewItemListener{

    private RecyclerView listPasswords;
    private ArrayList<MyPassword>myPasswordArrayList;
    private AdapterPasswordList adapterPasswordList;

    public PasswordSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Black_NoTitleBar);
        myPasswordArrayList =  getArguments().getParcelableArrayList("data");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_password_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listPasswords = view.findViewById(R.id.listPasswords);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);

       // listPasswords.addItemDecoration(itemDecoration);
        listPasswords.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPasswordList = new AdapterPasswordList(getActivity(),this);
        listPasswords.setAdapter(adapterPasswordList);
        adapterPasswordList.setMyPasswordArrayList(myPasswordArrayList);
    }

    @Override
    public void onDeleteClicked(int index, MyPassword myPassword) {
        myPasswordArrayList.remove(index);
        adapterPasswordList.setMyPasswordArrayList(myPasswordArrayList);
    }

    @Override
    public void onEditClicked(int index, MyPassword myPassword) {

    }
}
