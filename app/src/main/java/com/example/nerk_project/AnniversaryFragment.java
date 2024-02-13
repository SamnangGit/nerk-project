package com.example.nerk_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nerk_project.databinding.FragmentAnniversaryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnniversaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnniversaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentAnniversaryBinding binding;

    public AnniversaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnniversaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnniversaryFragment newInstance(String param1, String param2) {
        AnniversaryFragment fragment = new AnniversaryFragment();
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
        binding = FragmentAnniversaryBinding.inflate(inflater, container, false);
        binding.btnBack.setOnClickListener(view -> goBack());
        binding.btnStart.setOnClickListener(voew -> beginRelationship());

        getRelationshipDays();
        checkRelationshipsNode();
        return binding.getRoot();
    }

    private void goBack() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openOption();
    }

    public static AnniversaryFragment newInstance() {
        AnniversaryFragment fragment = new AnniversaryFragment();
        return fragment;
    }

    public void beginRelationship() {
        // Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the "relationships" node
        DatabaseReference ref = database.getReference("relationships");

        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Save the current date to the "relationships" node
        ref.push().setValue(currentDate);
    }

    public void getRelationshipDays() {
//        checkButtonTextAndPauseRelationship();
        // get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // get a reference to the "relationships" node
        DatabaseReference ref = database.getReference("relationships");

        // retrieve the relationship start date
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String startDateString = null;

                // iterate over the children of the "relationships" node
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    startDateString = childSnapshot.getValue(String.class);
                }

                // parse the start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date startDate = sdf.parse(startDateString);

                    // get the current date
                    Date currentDate = new Date();

                    // calculate the difference in days
                    long diff = currentDate.getTime() - startDate.getTime();
                    long relationshipDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    // convert the number of days in the relationship to a string
                    String relationshipDaysString = String.valueOf(relationshipDays);

                    binding.tvInLove.setText(relationshipDaysString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
//                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void checkRelationshipsNode() {
        // Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the "relationships" node
        DatabaseReference ref = database.getReference("relationships");

        // Attach a ValueEventListener to the "relationships" node
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The "relationships" node has data, change the text of the start button
                    binding.btnStart.setText("Pause");
                } else {
                    // The "relationships" node doesn't have data, set the text of the start button to default
                    binding.btnStart.setText("Start");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void checkButtonTextAndPauseRelationship() {
        // Check if the button text is "Pause"
        if (binding.btnStart.getText().toString().equals("Pause")) {
            // Get a reference to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Get the current date
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Get a reference to the "pauseDates" node
            DatabaseReference pauseRef = database.getReference("pauseDates");

            // Save the current date to the "pauseDates" node
            pauseRef.push().setValue(currentDate);

            binding.btnStart.setText("Start");
        }
    }



}