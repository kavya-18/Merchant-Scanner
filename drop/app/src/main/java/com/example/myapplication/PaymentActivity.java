package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {

    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        String str = intent.getStringExtra("Bottle");
        TextView bottleCountTxt = findViewById(R.id.bottleCountTxt);
        bottleCountTxt.setText(str+" Bottles");

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();


        userId = fAuth.getCurrentUser().getUid();

        final Button paymentBtn = findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                int totBottles = Integer.parseInt(str)+Integer.parseInt(String.valueOf(document.get("Bottles")));
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
        });
    }
}