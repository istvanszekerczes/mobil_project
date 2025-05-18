package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView; // Changed from EditText to TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getName();

    private FirebaseUser user;
    private TextView textViewName, textViewEmail; // Changed from EditText to TextView
    private FirebaseAuth mAuth;
    private static final int SECRET_KEY = 99;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.i(LOG_TAG, "ProfileActivity created");

        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secretKey != SECRET_KEY) {
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user.isAnonymous()) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Guest user has no profile!.");
            startActivity(new Intent(this, MainActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            finish();
            return;
        }

        textViewName = findViewById(R.id.textViewNameProfile); // Changed to textViewName
        textViewEmail = findViewById(R.id.textViewEmailProfile2); // Changed to textViewEmail
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            textViewName.setText(user.getDisplayName());  //display
            textViewEmail.setText(user.getEmail());    //display
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart ");
    }
}
