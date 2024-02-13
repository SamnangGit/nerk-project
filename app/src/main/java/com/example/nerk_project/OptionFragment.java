package com.example.nerk_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nerk_project.databinding.FragmentOptionBinding;
import com.example.nerk_project.databinding.FragmentPairCodeBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentOptionBinding binding;


    public OptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionFragment newInstance(String param1, String param2) {
        OptionFragment fragment = new OptionFragment();
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
        binding = FragmentOptionBinding.inflate(getLayoutInflater(), container, false);
        binding.btnAnniversary.setOnClickListener(view -> anniversary());
        binding.btnChat.setOnClickListener(view -> chat());
        binding.btnMemory.setOnClickListener(view -> memory());
        binding.btnHome.setOnClickListener(view -> home());
        binding.btnOut.setOnClickListener(view -> logOut());


        return binding.getRoot();
    }


    public static OptionFragment newInstance() {
        OptionFragment fragment = new OptionFragment();
        return fragment;
    }

    public void location() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openLocation();    }

    public void chat() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openChat();    }

    public void memory() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openMemory();    }


    public void home(){
    MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openHome();
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openLogin();
    }

    public void anniversary(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openAnniversary();
    }

}