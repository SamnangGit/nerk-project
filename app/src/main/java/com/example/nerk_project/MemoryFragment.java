package com.example.nerk_project;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nerk_project.databinding.FragmentChatBinding;
import com.example.nerk_project.databinding.FragmentMemoryBinding;
import com.example.nerk_project.model.FeedModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

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

    private FragmentActivity context;


//    FirebaseAuth user = FirebaseAuth.getInstance();
//    String userID = user.getUid();

    FragmentMemoryBinding binding;

    public MemoryFragment() {
        // Required empty public constructor
    }

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
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        binding.btnGo.setOnClickListener(view -> openPostFeed());
        binding.btnBack.setOnClickListener(view -> goBack());
        getRelationshipDays();
        populateView();

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

    private void populateView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("feeds").orderBy("time", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Error firestore", "Listen failed.", e);
                    return;
                }

                List<FeedModel> feedModels = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots) {
                    try {
                        FeedModel model = doc.toObject(FeedModel.class);

                        if (!model.getUserId().equals(retrievePartnerUID().trim()) && !model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                            continue;
                        }

                        feedModels.add(model);
                    } catch (RuntimeException ex) {
                        Log.w("Error deserialize", "Error deserializing document " + doc.getId(), ex);
                    }
                }

                ArrayAdapter<FeedModel> adapter = createAdapter(feedModels);
                ListView listView = context.findViewById(R.id.feedsList);
                listView.setAdapter(adapter);
                listView.setSelection(0);
            }
        });
    }

    private ArrayAdapter<FeedModel> createAdapter(List<FeedModel> models) {


        return new ArrayAdapter<FeedModel>(context, R.layout.message_layout, models) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.memory_layout, parent, false);
                }

                FeedModel model = getItem(position);
                ImageView memoryProfile = convertView.findViewById(R.id.memory_profile);
                TextView memoryUser = convertView.findViewById(R.id.memory_user);
                TextView memoryCaption = convertView.findViewById(R.id.memory_caption);
                ImageView memoryImage = convertView.findViewById(R.id.memory_image);
                TextView memoryTime = convertView.findViewById(R.id.memory_time);




                if (memoryUser != null) {
                    if (model.getUserId().equals(retrievePartnerUID().trim())) {
                        memoryUser.setText("Partner");
                        memoryProfile.setImageResource(R.drawable.avatar_girl);
                    } else if (model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        memoryUser.setText("You");
                        memoryProfile.setImageResource(R.drawable.boy_eight_bit);
                    }
                }
//
//                if(userID.equals(model.getUserId())){
//                    memoryUser.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//
//                } else {
//                    memoryUser.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                }

                if (memoryCaption != null) {
                    memoryCaption.setText(model.getUserInput());
                }
                if (memoryTime != null) {
                    Timestamp timestamp = model.getTime();
                    Date date = timestamp.toDate(); // Convert Timestamp to java.util.Date

                    // Create a SimpleDateFormat instance and format the date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDate = sdf.format(date);

                    memoryTime.setText(formattedDate);                }


                if (memoryImage != null) {
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference(model.getImagePath());
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext())
                                    .load(uri)
                                    .into(memoryImage);
                        }
                    });
                }


                return convertView;
            }
        };
    }


    public void getRelationshipDays() {
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

                    binding.tvRelationship.setText(relationshipDaysString);
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


    public String retrievePartnerUID(){
        try {
            File file = new File(getContext().getFilesDir(), "partnerUID.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String response = stringBuilder.toString();

            Log.d("Builder: ", response);

            // Remove invalid characters
            response = response.replace(".", "")
                    .replace("$", "")
                    .replace("[", "")
                    .replace("]", "")
                    .replace("#", "")
                    .replace("/", "");

            int count = 0;

            for (int i = 0; i < response.length(); i++) {

                // Checking the character for not being a
                // letter,digit or space
                if (!Character.isDigit(response.charAt(i))
                        && !Character.isLetter(response.charAt(i))
                        && !Character.isWhitespace(response.charAt(i))) {
                    // Incrementing the countr for spl
                    // characters by unity
                    count++;
                }
            }

            // When there is no special character encountered
            if (count == 0)
                Log.d("specialChar", "No Special Characters found.");
            else
                Log.d("specialChar", "Special Characters found.");

            Log.d("response: ", response);
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}