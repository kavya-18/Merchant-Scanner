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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private boolean passwordShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        final EditText username = findViewById(R.id.usernameET);
        final EditText password = findViewById(R.id.passwordET);
        final ImageView passwordIcon= findViewById(R.id.passwordIcon);
        final TextView signUpBtn = findViewById(R.id.signUpTxt);
        final AppCompatButton loginButton = findViewById(R.id.loginButton);
        ProgressBar loginProgressBar = findViewById(R.id.loginProgressBar);

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

        loginButton.setOnClickListener(view -> {
            final String getPassword = password.getText().toString();
            final String getEmailTxt = username.getText().toString();

            if(TextUtils.isEmpty(getEmailTxt)){
                username.setError("Username is Required");
                return;
            }
            if(TextUtils.isEmpty(getPassword)){
                password.setError("Password is Required");
                return;
            }
            if(getPassword.length() < 6){
                password.setError("Password Must be >= 6 Characters");
                return;
            }
            loginProgressBar.setVisibility(View.VISIBLE);
            fAuth.signInWithEmailAndPassword(getEmailTxt,getPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"Login Successfully!",Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(Login.this,HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this,"Error ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        });

        signUpBtn.setOnClickListener(view -> startActivity(new Intent(Login.this,Register.class)));
    }
}