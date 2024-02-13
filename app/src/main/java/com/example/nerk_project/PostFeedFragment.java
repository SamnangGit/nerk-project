package com.example.nerk_project;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nerk_project.databinding.FragmentChatBinding;
import com.example.nerk_project.databinding.FragmentPostFeedBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentPostFeedBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    public PostFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFeedFragment newInstance(String param1, String param2) {
        PostFeedFragment fragment = new PostFeedFragment();
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
        binding = FragmentPostFeedBinding.inflate(inflater, container, false);
        binding.btnImg.setOnClickListener(view -> openFileChooser());
        binding.btnPost.setOnClickListener(view -> saveImage());
        binding.btnBack.setOnClickListener(view -> goBack());

        checkUser();

        return binding.getRoot();    }



    public static PostFeedFragment newInstance() {
        PostFeedFragment fragment = new PostFeedFragment();
        return fragment;
    }

    public void goBack(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openMemory();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Set the image to the ImageView
            binding.previewImage.setImageURI(mImageUri);
        }
    }

    private void saveImage() {
        if (mImageUri != null) {
            // Get a reference to the storage service, using the link to your Firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

            // Create a reference to the file you want to upload
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            // Upload the file to Firebase
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // The image was uploaded successfully

                            // Get a reference to Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Get the user input from the EditText

                            String userInput = binding.edtInput.getText().toString();

                            // Get the current time
                            Timestamp timestamp = new Timestamp(new Date());

                            // Get the user ID
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Create a new document with the user input, time, image path, and user ID
                            Map<String, Object> docData = new HashMap<>();
                            docData.put("userInput", userInput);
                            docData.put("time", timestamp);
                            docData.put("imagePath", fileReference.getPath());
                            docData.put("userId", userId);

                            // Add the document to a collection in Firestore
                            db.collection("feeds").add(docData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("uploaded", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Error", "Error adding document", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                        }
                    });
        } else {
            // No file was selected
        }

//        binding.edtInput.setText("");
        Toast.makeText(getActivity(), "Post uploaded", Toast.LENGTH_SHORT).show();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openMemory();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void checkUser(){
        FirebaseAuth user = FirebaseAuth.getInstance();
        String userID = user.getUid();
        if(userID.equals("KexuveflI8bCQzKeN3zqnE7YjTU2")){
            binding.memoryProfile.setImageResource(R.drawable.avatar_girl);
            binding.memoryUser.setText("Kv");
        }else if(userID.equals("HdzXXsZCuMYsMs66zvzL13n2naw2")){
            binding.memoryProfile.setImageResource(R.drawable.boy_eight_bit);
            binding.memoryUser.setText("Ps");
        }

    }
}