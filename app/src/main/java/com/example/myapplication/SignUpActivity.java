package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = SignUpActivity.class.getPackage().toString();
    private static final String LOG_TAG = SignUpActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secretKey != SECRET_KEY) {
            finish();
        }
        emailEditText = findViewById(R.id.editTextEmail);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email =  sharedPreferences.getString("email", "");

        emailEditText.setText(email);

        firebaseAuth =  FirebaseAuth.getInstance();






        Log.i(LOG_TAG, "onCreate");
    }
    public void signUp(View view) {
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Passwords do not match!");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "User created successfully!");
                    goToFootballPage();
                } else {
                    Log.d(LOG_TAG, "User wasn't created succesfully!");
                    Toast.makeText(SignUpActivity.this, "User wasn't created succesfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i(LOG_TAG, "Signed up succesfully " + username);
    }

    public void back(View view) {
        finish();
    }

    public void goToFootballPage() {
        Intent intent = new Intent(this, FootballPageActivity.class);
        startActivity(intent);
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

        ImageView imageView = findViewById(R.id.imageView);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation rotateAnimation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.rotate);
                imageView.startAnimation(rotateAnimation);
            }
        }, 1000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart ");
    }
}