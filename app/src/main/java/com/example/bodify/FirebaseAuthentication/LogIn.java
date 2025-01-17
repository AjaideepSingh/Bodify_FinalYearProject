package com.example.bodify.FirebaseAuthentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bodify.Management;
import com.example.bodify.DiaryRefreshService;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private EditText emailAddress, password;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Log In Form");
        setContentView(R.layout.activity_log_in);
        if (mAuth.getCurrentUser() != null) {
            startService(new Intent(LogIn.this, DiaryRefreshService.class));
            startActivity(new Intent(LogIn.this, Management.class));
            return;
        }
        Button logIn = findViewById(R.id.logInButton);
        emailAddress = findViewById(R.id.userNameSignUp);
        password = findViewById(R.id.passwordTextField);
        TextView createAcc = findViewById(R.id.createNewAccount);
        createAcc.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUp.class));
        });
        logIn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(emailAddress.getText().toString().trim())) {
                emailAddress.setError("Email Address is required.");
                emailAddress.requestFocus();
            } else if (password.getText().toString().length() < 6) {
                password.setError("Password length must be >= 6 characters");
                password.requestFocus();
            } else {
                mAuth.signInWithEmailAndPassword(emailAddress.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startService(new Intent(LogIn.this, DiaryRefreshService.class));
                        startActivity(new Intent(getApplicationContext(), Management.class));
                        emailAddress.setText("");
                        password.setText("");
                    } else {
                        Toast.makeText(LogIn.this, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LogIn.class));
    }
}