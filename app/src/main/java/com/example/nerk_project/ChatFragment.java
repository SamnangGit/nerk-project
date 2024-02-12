package com.example.nerk_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerk_project.databinding.FragmentChatBinding;
import com.example.nerk_project.model.ChatModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentChatBinding binding;


    private DatabaseReference databaseReference;
    private FirebaseListAdapter<ChatModel> adapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        binding = FragmentChatBinding.inflate(inflater, container, false);
        binding.sendButton.setOnClickListener(view -> sendMessage());
        binding.btnBack.setOnClickListener(view -> goOption());

        displayMessageHistory();
        return binding.getRoot();

    }

    private void goOption() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openOption();
    }

    private void sendMessage() {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        databaseReference.push().setValue(new ChatModel(binding.input.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        binding.input.setText("");

        FirebaseListOptions<ChatModel> options = new FirebaseListOptions.Builder<ChatModel>()
                .setLayout(R.layout.message_layout)
                .setQuery(databaseReference, ChatModel.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseListAdapter<ChatModel>(options) {
            @Override
            protected void populateView(View v, ChatModel model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                messageTime.setText(sdf.format(new Date(model.getMessageTime())));
            }
        };

        binding.listOfMessages.setAdapter(adapter);
        binding.listOfMessages.setSelection(adapter.getCount() - 1); // Scroll to bottom

    }

    private void displayMessageHistory() {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        FirebaseListOptions<ChatModel> options = new FirebaseListOptions.Builder<ChatModel>()
                .setLayout(R.layout.message_layout)
                .setQuery(databaseReference, ChatModel.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseListAdapter<ChatModel>(options) {
            @Override
            protected void populateView(View v, ChatModel model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                messageTime.setText(sdf.format(new Date(model.getMessageTime())));
            }
        };

        binding.listOfMessages.setAdapter(adapter);
        binding.listOfMessages.setSelection(adapter.getCount() - 1); // Scroll to bottom
    }



    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }
}