package com.example.nerk_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.nerk_project.databinding.FragmentChatBinding;
import com.example.nerk_project.databinding.FragmentMemoryBinding;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    FragmentMemoryBinding binding;

    public MemoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoryFragment newInstance(String param1, String param2) {
        MemoryFragment fragment = new MemoryFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        binding.sendButton.setOnClickListener(view -> openPostFeed());
        binding.btnBack.setOnClickListener(view -> goBack());

        return binding.getRoot();
    }

    public void openPostFeed(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openPostFeed();
    }

    public static MemoryFragment newInstance() {
        MemoryFragment fragment = new MemoryFragment();
        return fragment;
    }

    public void goBack(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openOption();
    }

//    public void loadImage() {
//        // Get a reference to the storage service, using the link to your Firebase storage
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//
//        // Create a reference to the file you want to download
//        StorageReference fileReference = storageReference.child("uploads/your_image.jpg");
//
//        // Create a reference to your ImageView
//        ImageView imageView = findViewById(R.id..);
//
//        // Download the file using Glide
//        Glide.with(this /* context */)
//                .using(new FirebaseImageLoader())
//                .load(fileReference)
//                .into(imageView);
//    }

}