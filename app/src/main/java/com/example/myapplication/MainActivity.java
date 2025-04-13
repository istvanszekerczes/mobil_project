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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText emailEditText;
    EditText passwordEditText;

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        Log.i(LOG_TAG, "onCreate");

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "User logged in successfully!");
                    goToFootballPage();
                } else {
                    Log.d(LOG_TAG, "User wasn't logged in succesfully!");
                    Toast.makeText(MainActivity.this, "Incorrect credentials. Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });



        Log.i(LOG_TAG, "Logged in succesfully " + email);
    }

    public void loginAsGuest(View view) {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Anonymous user logged in successfully!");
                    goToFootballPage();
                } else {
                    Log.d(LOG_TAG, "Anonymous user wasn't logged in succesfully!");
                    Toast.makeText(MainActivity.this, "User wasn't created succesfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToFootballPage() {
        Intent intent = new Intent(this, FootballPageActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", emailEditText.getText().toString());
        editor.apply();

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
                Animation rotateAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
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