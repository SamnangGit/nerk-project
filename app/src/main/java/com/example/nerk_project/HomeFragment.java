package com.example.nerk_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;


import com.example.nerk_project.databinding.FragmentHomeBinding;
import com.example.nerk_project.databinding.FragmentLoginBinding;
import com.example.nerk_project.databinding.InputTodoLayoutBinding;
import com.example.nerk_project.databinding.UpdateTodoLayoutBinding;
import com.example.nerk_project.model.ToDoModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    InputTodoLayoutBinding inputBinding;
    UpdateTodoLayoutBinding updateBinding;
    ListView listView;
    private static CustomAdapter adapter;
    ArrayList<ToDoModel> dataModels;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentHomeBinding binding;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        binding.btnBack.setOnClickListener(view -> goBack());
        binding.btnSet.setOnClickListener(view -> todoOperation());

        // 2- Data source
        dataModels = new ArrayList<>();

        return binding.getRoot();

    }

    private void  goBack(){
        openFragment(LoginFragment.newInstance());
    }

    private void openFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow();
    }

    private void todoOperation(){
        inputBinding = InputTodoLayoutBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(getContext())
                .setTitle("Enter Task")
                .setView(inputBinding.getRoot())
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setListItem(inputBinding.edtTime.getText().toString(), inputBinding.edtTitle.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }

    private void setListItem(String time, String title){

        binding.listViewTodo.setAdapter(adapter);
        dataModels.add(new ToDoModel(time, title));

        // 3- Adapter
        adapter = new CustomAdapter(dataModels, getActivity().getApplicationContext());
        binding.listViewTodo.setAdapter(adapter);

        binding.listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String time = adapter.getItem(i).getTime();
                String title = adapter.getItem(i).getTitle();

//                Log.d("item", dataModels.get(i).getTime());

                updateBinding = UpdateTodoLayoutBinding.inflate(getLayoutInflater());

                updateBinding.edtTimeUpdate.setText(time);
                updateBinding.edtTitleUpdate.setText(title);

                new AlertDialog.Builder(getContext())
                        .setTitle("Update Task")
                        .setView(updateBinding.getRoot())
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ind) {
                                dataModels.remove(i);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ind) {
                                dataModels.set(i, new ToDoModel(updateBinding.edtTimeUpdate.getText().toString(),
                                                                updateBinding.edtTitleUpdate.getText().toString()));
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .create().show();
            }
        });
    }

}