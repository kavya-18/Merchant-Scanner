package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private boolean passwordShowing = false;
    private boolean confPassShowing = false;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser()!=null){
            Intent intent = new Intent(Register.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        final EditText email = findViewById(R.id.emailET);
        final EditText mobile = findViewById(R.id.phoneNumberET);

        final EditText fullName = findViewById(R.id.fullNameET);

        final EditText password = findViewById(R.id.passwordET);
        final EditText confPass = findViewById(R.id.conPasswordET);

        final ImageView passwordIcon = findViewById(R.id.passwordIcon);
        final ImageView confPassIcon = findViewById(R.id.conPasswordIcon);

        final AppCompatButton signUpBtn = findViewById(R.id.signUpButton);
        final TextView signInBtn = findViewById(R.id.signInTxt);

        ProgressBar progressBar = findViewById(R.id.registerProgressBar);

        passwordIcon.setOnClickListener(view -> {
            if(passwordShowing){
                passwordShowing = false;
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordIcon.setImageResource(R.drawable.password_show);
            }
            else{
                passwordShowing = true;
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordIcon.setImageResource(R.drawable.password_hide);
            }
            password.setSelection(password.length());
        });

        confPassIcon.setOnClickListener(view -> {
            if(confPassShowing){
                confPassShowing = false;
                confPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confPassIcon.setImageResource(R.drawable.password_show);
            }
            else{
                confPassShowing = true;
                confPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confPassIcon.setImageResource(R.drawable.password_hide);
            }
            confPass.setSelection(confPass.length());
        });

        signUpBtn.setOnClickListener(view -> {

            final String getPassword = password.getText().toString();
            final String getConfPassword = confPass.getText().toString();
            final String getEmailTxt = email.getText().toString();
            final String getFullName = fullName.getText().toString();
            final String getMobile = mobile.getText().toString();

            if(TextUtils.isEmpty(getEmailTxt)){
                email.setError("Email is Required");
                return;
            }
            if(TextUtils.isEmpty(getPassword)){
                password.setError("Password is Required");
                return;
            }
            if(TextUtils.isEmpty(getConfPassword)){
                confPass.setError("Password is Required");
                return;
            }
            if(getPassword.length() < 6){
                password.setError("Password Must be >= 6 Characters");
                return;
            }
            if(getConfPassword.length() < 6){
                confPass.setError("Confirm Password Must be >= 6 Characters");
                return;
            }
            if(!getPassword.equals(getConfPassword)){
                password.setError("Mismatch");
                confPass.setError("Mismatch");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(getEmailTxt,getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("user").document(userID);
                        HashMap<String,Object> user = new HashMap<>();
                        user.put("Full Name",getFullName);
                        user.put("Email",getEmailTxt);
                        user.put("Mobile Number",getMobile);
                        user.put("Bottles",0);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Register.this, "User Created!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(Register.this,SubscriptionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Register.this,"Error ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        });
        signInBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        });
    }
}