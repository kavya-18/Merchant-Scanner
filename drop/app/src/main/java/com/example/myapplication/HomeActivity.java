package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    String userId;
    ImageView qrImage;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatButton logoutBtn = findViewById(R.id.logoutBtn);
        TextView totBottleTxt = findViewById(R.id.totBottleTxt);
        qrImage = findViewById(R.id.qrCodeId);
        Button addMoreBottles = findViewById(R.id.addMoreId);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        AppCompatButton scanBtn = findViewById(R.id.scanBtn);

        userId = fAuth.getCurrentUser().getUid();


        String myText = userId.trim();
        //initializing MultiFormatWriter for QR code
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            //BitMatrix class to encode entered text and set Width & Height
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400,400);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            qrImage.setImageBitmap(mBitmap);//Setting generated QR code to imageView
        } catch (WriterException e) {
            e.printStackTrace();
        }
        DocumentReference documentReference = fStore.collection("user").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();;
                    if (document.exists()) {
                        int totBottles = Integer.parseInt(String.valueOf(document.get("Bottles")));
                        totBottleTxt.setText("Total Bottles : "+totBottles);
                    }
                    else {
                        Log.d("MSG","No such document");
                    }
                }
            }
        });
        ActivityResultLauncher< ScanOptions > barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String rmId = result.getContents();
            removeBottleFrmUser(rmId);
            addBottleForUser(userId);
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Result");
            builder.setMessage("One Bottle is Added Successfully!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setPrompt("Volume up to flash on");
                options.setBeepEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(CaptureActivity.class);
                barLauncher.launch(options);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
        addMoreBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SubscriptionActivity.class));
            }
        });
    }

    private void addBottleForUser(String userId) {
        DocumentReference documentReference = fStore.collection("user").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();;
                    if (document.exists()) {
                        HashMap<String,Object> user = new HashMap<>();
                        user.put("Full Name",document.get("Full Name"));
                        user.put("Email",document.get("Email"));
                        user.put("Mobile Number",document.get("Mobile Number"));
                        int totBottles = Integer.parseInt(String.valueOf(document.get("Bottles")))+Integer.parseInt("1");
                        user.put("Bottles",totBottles);
                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            }
                        });
                    }
                    else {
                        Log.d("Data123","No such document");
                    }
                }
            }
        });
    }

    private void removeBottleFrmUser(String rmId) {
        DocumentReference documentReference = fStore.collection("user").document(rmId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();;
                    if (document.exists()) {
                        HashMap<String,Object> user = new HashMap<>();
                        user.put("Full Name",document.get("Full Name"));
                        user.put("Email",document.get("Email"));
                        user.put("Mobile Number",document.get("Mobile Number"));
                        int totBottles = Integer.parseInt(String.valueOf(document.get("Bottles")))-Integer.parseInt("1");
                        user.put("Bottles",totBottles);
                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            }
                        });
                    }
                    else {
                        Log.d("Data123","No such document");
                    }
                }
            }
        });
    }
}