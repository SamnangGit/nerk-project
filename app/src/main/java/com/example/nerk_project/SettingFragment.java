package com.example.nerk_project;

import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nerk_project.databinding.FragmentLoginBinding;
import com.example.nerk_project.databinding.FragmentSettingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @NonNull FragmentSettingBinding binding;

    private FirebaseDatabase database;


    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(getLayoutInflater(), container, false);
        binding.btnReturnHome.setOnClickListener(view -> returnBack());
        binding.btnSetColor.setOnClickListener(view -> setColorCode());
        binding.imgWifi.setOnClickListener(view -> setWifi());
        binding.imgReset.setOnClickListener(view -> resetAllData());

        return binding.getRoot();


    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    MainActivity mainActivity (){
        return (MainActivity) getActivity();
    }

    private void returnBack(){ mainActivity().openHome(); }

    private void setColorCode(){
        String colorCode = binding.edtColor.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String partnerUid = "";
        String uid = user.getUid();

        if(uid.equals("HdzXXsZCuMYsMs66zvzL13n2naw2")){
            partnerUid = "KexuveflI8bCQzKeN3zqnE7YjTU2";
        }else if(uid.equals("KexuveflI8bCQzKeN3zqnE7YjTU2")){
            partnerUid = "HdzXXsZCuMYsMs66zvzL13n2naw2";
        }

        database = FirebaseDatabase.getInstance();
        database.getReference()
                .child("users")
                .child(partnerUid)
                .child("123456")
                .child("touch")
                .child("color")
                .setValue(colorCode);

        Toast.makeText(mainActivity().getApplicationContext()
                , "Done", Toast.LENGTH_SHORT).show();
    }

    private void setWifi(){
        String url = "http://192.168.4.1";
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(urlIntent);
    }

    private void resetAllData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String partnerUid = "";
        if(uid.equals("HdzXXsZCuMYsMs66zvzL13n2naw2")){
            partnerUid = "KexuveflI8bCQzKeN3zqnE7YjTU2";
        }else if(uid.equals("KexuveflI8bCQzKeN3zqnE7YjTU2")){
            partnerUid = "HdzXXsZCuMYsMs66zvzL13n2naw2";
        }

        database = FirebaseDatabase.getInstance();
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("123456")
                .child("todos")
                .setValue("");

        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("123456")
                .child("touch")
                .child("count")
                .setValue(0);

        database = FirebaseDatabase.getInstance();
        database.getReference()
                .child("users")
                .child(partnerUid)
                .child("123456")
                .child("todos")
                .setValue("");

        database.getReference()
                .child("users")
                .child(partnerUid)
                .child("123456")
                .child("touch")
                .child("count")
                .setValue(0);

        Toast.makeText(mainActivity().getApplicationContext()
                , "Done", Toast.LENGTH_SHORT).show();
    }
}