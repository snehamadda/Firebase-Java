package com.example.firebaselearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaselearning.R;
import com.example.firebaselearning.models.User;
import com.example.firebaselearning.utils.Utils;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private EditText passwordET, emailET;
    private LoginActivity context;
    private FrameLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;

        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        firebaseDatabase = firebaseInstance.getReference();
        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);
        progressOverlay = findViewById(R.id.progressOverlay);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if (password.isEmpty() || email.isEmpty()) {
                    showToast("Please enter email and password");
                    return;
                }
                getUser(email, password);
            }
        });
    }

    private void getUser(final String email, final String pass) {
        try {
            DatabaseReference users = firebaseDatabase.child("users");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot dataSnapshot : children) {
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String password = dataSnapshot.child("password").getValue(String.class);
                        if (userEmail != null && userEmail.equals(email) && password != null && password.equals(pass)) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                            String userId = dataSnapshot.getKey();
                            User user = new User(name, email, password);
                            user.setId(userId);
                            showToast("Welcome " + user.getName());
                            startHome(user);
                        } else {
                            showToast("User not exists with this email");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed: " + error);
                    Utils.setVisibility(progressOverlay, false);
                }
            });
        } catch (Exception e) {
            showToast("Error: " + e.toString());
        }
    }

    private void startHome(User user) {
        Utils.setVisibility(progressOverlay, true);
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
