package com.example.firebaselearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaselearning.R;
import com.example.firebaselearning.models.User;
import com.example.firebaselearning.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private EditText nameET, emailET, passwordET;
    private RegisterActivity context;
    private FrameLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = RegisterActivity.this;

        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        firebaseDatabase = firebaseInstance.getReference();
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        progressOverlay = findViewById(R.id.progressOverlay);
        Button registerBtn = findViewById(R.id.registerBtn);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if (name.isEmpty() || email.isEmpty()) {
                    showToast("Please enter user name and email");
                    return;
                }
                User user = new User(name, email, password);
                registerUser(user);
            }
        });
    }

    private void registerUser(User user) {
        Utils.setVisibility(progressOverlay, true);
        String userId = firebaseDatabase.push().getKey();
        assert userId != null;
        firebaseDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        showToast("Success");
                        Utils.setVisibility(progressOverlay, false);
                        nameET.getText().clear();
                        emailET.getText().clear();
                        passwordET.getText().clear();
                        startLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        showToast("Failed");
                        Utils.setVisibility(progressOverlay, false);
                    }
                });
    }

    private void startLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}