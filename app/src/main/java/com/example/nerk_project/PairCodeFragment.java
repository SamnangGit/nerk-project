package com.example.nerk_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nerk_project.databinding.FragmentPairCodeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PairCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PairCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentPairCodeBinding binding;

    private MultiFormatReader multiFormatReader = new MultiFormatReader();



    public PairCodeFragment() {
        // Required empty public constructor
    }

    public static PairCodeFragment newInstance(String param1, String param2) {
        PairCodeFragment fragment = new PairCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PairCodeFragment newInstance() {
        PairCodeFragment fragment = new PairCodeFragment();
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
        // return inflater.inflate(R.layout.fragment_login, container, false);
        binding = FragmentPairCodeBinding.inflate(getLayoutInflater(), container, false);
        binding.btnPair.setOnClickListener(view -> scanCode());
        ImageView imageView = binding.ivQrCode;
        Bitmap qrCodeBitmap = generateQRCode(getUID());
        imageView.setImageBitmap(qrCodeBitmap);

        return binding.getRoot();
    }


    public String getUID(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return uid;
    }

    public Bitmap generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setPartnerUID(String partnerUID){
        String uid = getUID();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("partnerUID").setValue(partnerUID);
    }

    public void setPartnerUIDInLocalStore(String partnerUID){
        try {
            // Define the File Path and its Name
            File file = new File(getContext().getFilesDir(), "partnerUID.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(partnerUID);
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }


    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {

            String partnerUID = result.getContents();
//            setPartnerUID(partnerUID);
            setPartnerUIDInLocalStore(partnerUID);
            Toast.makeText(getContext(), "Pairing successful", Toast.LENGTH_SHORT).show();
//            redirect to home
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.openOption();
            ;


        }

    });


}