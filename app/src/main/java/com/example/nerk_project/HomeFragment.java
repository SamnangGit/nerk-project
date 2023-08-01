package com.example.nerk_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private FirebaseDatabase database;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    InputTodoLayoutBinding inputBinding;
    UpdateTodoLayoutBinding updateBinding;
    ListView listView;
    private static CustomAdapter adapter;
    ArrayList<ToDoModel> dataModels;
    ArrayList<ToDoModel> partnerDataModels;


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
        binding.btnSetting.setOnClickListener(view -> openSetting());


//        binding.tcCount.setText(Integer.toString(getCount()));

        getCount();

        partnerDataModels = new ArrayList<>();
        binding.btnTitleHome.setOnClickListener(view -> fetchPartnerData(partnerDataModels));


        // 2- Data source
        dataModels = new ArrayList<>();

//        retrieveFirebaseData(dataModels);
//        getFirebaseData();
        fecthData(dataModels);
        Log.d("dataModel", "amount");
        Log.d("dataModel", Integer.toString(dataModels.size()));


        return binding.getRoot();

    }

    private void goBack(){
        FirebaseAuth.getInstance().signOut();
        openFragment(LoginFragment.newInstance());
    }

    private void openSetting(){
        openFragment(SettingFragment.newInstance());
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
                        String time = inputBinding.edtTime.getText().toString();
                        String title = inputBinding.edtTitle.getText().toString();
                        setListItem(time, title);

                        Gson gson = new Gson();
                        String json = gson.toJson(dataModels);
                        database = FirebaseDatabase.getInstance();
                        database.getReference()
                                .child("users")
                                .child(user.getUid())
                                .child("123456")
                                .child("todos")
//                                .push()
                                .setValue(json);
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

    private void retrieveFirebaseData(ArrayList<ToDoModel> toDoList){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<HashMap<String, String>>> t = new GenericTypeIndicator<ArrayList<HashMap<String, String>>>() {};
                ArrayList<HashMap<String, String>> yourArray = snapshot.getValue(t);
//                ArrayList<ToDoModel> toDoList = new ArrayList<>();
                for (HashMap<String, String> map : yourArray) {
                    String time = map.get("time");
                    String title = map.get("title");
                    ToDoModel toDo = new ToDoModel(time, title);
                    toDoList.add(toDo);
                }
                // Do something with toDoList
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        };
        mDatabase.child("users/ViwijDg30YPOf3UTCKCIsFc0XuK2/123456")
                .limitToLast(1)
                .addValueEventListener(postListener);
    }

    private void getFirebaseData(){
        Task<DataSnapshot> task = database.getReference()
                .child("users")
                .child(user.getUid())
                .child("123456")
                .get();
        task.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("data", value);
            }
        });
    }

    public void fecthData(ArrayList<ToDoModel> todoList) {
        todoList.clear();
        database = FirebaseDatabase.getInstance();
        database.getReference()
                .child("users")
                .child(user.getUid())
                .child("123456")
                .child("todos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            String fecthData = String.valueOf(task.getResult().getValue());
//                            Log.d("data", fecthData);
                            Gson gson = new Gson();

                            Type listType = new TypeToken<ArrayList<ToDoModel>>(){}.getType();
                            ArrayList<ToDoModel> todos = gson.fromJson(fecthData, listType);

                            if(todos != null){
                                for (ToDoModel todo : todos) {
                                    Log.d("item", todo.getTime());
//                                todoList.add(new ToDoModel(todo.getTime(), todo.getTitle()));
                                    Log.d("dataModel", "amount before");
                                    Log.d("dataModel", Integer.toString(todoList.size()));
                                    binding.listViewTodo.setAdapter(adapter);
                                    dataModels.add(new ToDoModel(todo.getTime(), todo.getTitle()));

                                    // 3- Adapter
                                    adapter = new CustomAdapter(dataModels, getActivity().getApplicationContext());
                                    binding.listViewTodo.setAdapter(adapter);

                                    binding.listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String time = adapter.getItem(i).getTime();
                                            String title = adapter.getItem(i).getTitle();

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

                                                            Gson gson = new Gson();
                                                            String json = gson.toJson(dataModels);
                                                            database = FirebaseDatabase.getInstance();
                                                            database.getReference()
                                                                    .child("users")
                                                                    .child(user.getUid())
                                                                    .child("123456")
                                                                    .child("todos")
                                                                    .setValue(json);
                                                        }
                                                    })
                                                    .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int ind) {
                                                            dataModels.set(i, new ToDoModel(updateBinding.edtTimeUpdate.getText().toString(),
                                                                    updateBinding.edtTitleUpdate.getText().toString()));
                                                            adapter.notifyDataSetChanged();

                                                            Gson gson = new Gson();
                                                            String json = gson.toJson(dataModels);
                                                            database = FirebaseDatabase.getInstance();
                                                            database.getReference()
                                                                    .child("users")
                                                                    .child(user.getUid())
                                                                    .child("123456")
                                                                    .child("todos")
                                                                    .setValue(json);
                                                        }
                                                    })
                                                    .create().show();
                                        }
                                    });
                                }

                            }

                        }
                    }
                });

    }

    public void fetchPartnerData(ArrayList<ToDoModel> todoList){
        todoList.clear();

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
                .child("todos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            String fecthData = String.valueOf(task.getResult().getValue());
                            Gson gson = new Gson();
                            Log.d("click", fecthData);


                            Type listType = new TypeToken<ArrayList<ToDoModel>>(){}.getType();
                            ArrayList<ToDoModel> todos = gson.fromJson(fecthData, listType);

                            Log.d("btnTitle", binding.btnTitleHome.getText().toString());

                            if(todos != null && binding.btnTitleHome.getText().toString().equals("NERK")){
                                binding.btnTitleHome.setText("PARTNER");
                                binding.btnSet.setClickable(false);
                                binding.listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    }
                                });

                                for (ToDoModel todo : todos) {
                                    Log.d("item", todo.getTime());
                                    Log.d("dataModel", "amount before");
                                    Log.d("dataModel", Integer.toString(todoList.size()));
                                    binding.listViewTodo.setAdapter(adapter);
                                    partnerDataModels.add(new ToDoModel(todo.getTime(), todo.getTitle()));

                                    // 3- Adapter
                                    adapter = new CustomAdapter(partnerDataModels, getActivity().getApplicationContext());
                                    binding.listViewTodo.setAdapter(adapter);

                                }

                            }
                            else{
                                binding.btnSet.setClickable(true);
                                fecthData(dataModels);
                                binding.btnTitleHome.setText("NERK");
                            }

                        }
                    }
                });

    }


    private void getCount(){
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
                .child(user.getUid())
                .child("123456")
                .child("touch")
                .child("count")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }else{
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            int count = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                            int pbCount = count % 10;

                            binding.tcCount.setText(Integer.toString(count));
                            binding.progressBar.setProgress(pbCount);
                            if(count >= 10){
                                binding.imageViewCircleTwo.setImageResource(R.drawable.circle_shape);
                                if(count >= 30){
                                    binding.imageViewRectangle.setImageResource(R.drawable.rectangle_shape);
                                    if(count >= 40){
                                        binding.imageViewCircleOne.setImageResource(R.drawable.circle_shape);
                                    }
                                }
                            }

                        }
                    }
                });
    }

}