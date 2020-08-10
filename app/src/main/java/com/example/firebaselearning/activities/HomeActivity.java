package com.example.firebaselearning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaselearning.R;
import com.example.firebaselearning.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private EditText passwordET, emailET, nameET;
    private DatabaseReference firebaseDatabase;
    private HomeActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;

        final Button updateBtn = findViewById(R.id.updateBtn);
        final Button deleteBtn = findViewById(R.id.deleteBtn);
        nameET = findViewById(R.id.nameET);
        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);

        Intent myIntent = getIntent();
        User user = (User) myIntent.getSerializableExtra("user");
        if (user != null) {
            nameET.setText(user.getName());
            emailET.setText(user.getEmail());
            passwordET.setText(user.getPassword());
        }
        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        firebaseDatabase = firebaseInstance.getReference();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameET.getText().toString();
                final String email = emailET.getText().toString();
                final String pass = passwordET.getText().toString();

                final DatabaseReference users = firebaseDatabase.child("users");
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for (DataSnapshot dataSnapshot : children) {
                            String userEmail = dataSnapshot.child("email").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);
                            if (userEmail != null && userEmail.equals(email) && password != null && password.equals(pass)) {
                                String userId = dataSnapshot.getKey();
                                if (userId != null) {
                                    users.child(userId).child("name").setValue(name);
                                    users.child(userId).child("password").setValue(pass);
                                    users.child(userId).child("email").setValue(email);
                                    showToast("Updated successfully");
                                    nameET.getText().clear();
                                    passwordET.getText().clear();
                                    emailET.getText().clear();
                                }
                            } else {
                                showToast("User not exists with this email");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameET.getText().toString();
                final String email = emailET.getText().toString();
                final String pass = passwordET.getText().toString();

                final DatabaseReference users = firebaseDatabase.child("users");
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for (DataSnapshot dataSnapshot : children) {
                            String userEmail = dataSnapshot.child("email").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);
                            if (userEmail != null && userEmail.equals(email) && password != null && password.equals(pass)) {
                                String userId = dataSnapshot.getKey();
                                if (userId != null) {
                                    users.child(userId).child("name").removeValue();
                                    users.child(userId).child("password").removeValue();
                                    users.child(userId).child("email").removeValue();
                                    showToast("Deleted successfully");
                                    nameET.getText().clear();
                                    passwordET.getText().clear();
                                    emailET.getText().clear();
                                }
                            } else {
                                showToast("User not exists with this email");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}