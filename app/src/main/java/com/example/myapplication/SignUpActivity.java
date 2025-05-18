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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

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
    private FirebaseFirestore firebaseFirestore;
    private boolean isSignUpInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secretKey != SECRET_KEY) {
            finish();
            return;
        }

        emailEditText = findViewById(R.id.editTextEmail);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        emailEditText.setText(email);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void signUp(View view) {
        if (isSignUpInProgress) {
            Log.d(LOG_TAG, "Sign up already in progress");
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Passwords do not match!");
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        isSignUpInProgress = true;

        firebaseFirestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            isSignUpInProgress = false;
                            Toast.makeText(SignUpActivity.this, "This username is already taken. Please choose another one.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Creating account...", Toast.LENGTH_SHORT).show();
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, authTask -> {
                                        try {
                                            if (authTask.isSuccessful()) {
                                                Log.d(LOG_TAG, "User created successfully!");
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                if (user != null) {
                                                    saveUserDataToFirestore(user.getUid(), email, username);
                                                } else {
                                                    isSignUpInProgress = false;
                                                    Log.e(LOG_TAG, "User object is null after successful authentication.");
                                                    if (!isFinishing() && !isDestroyed()) {
                                                        Toast.makeText(SignUpActivity.this, "Error: Could not retrieve user information.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            } else {
                                                isSignUpInProgress = false;
                                                Log.d(LOG_TAG, "User wasn't created successfully!");
                                                String errorMessage = "Registration failed";
                                                if (authTask.getException() != null) {
                                                    errorMessage += ": " + authTask.getException().getMessage();
                                                    Log.e(LOG_TAG, "Firebase Auth Error", authTask.getException());
                                                }
                                                if (!isFinishing() && !isDestroyed()) {
                                                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } catch (Exception e) {
                                            isSignUpInProgress = false;
                                            Log.e(LOG_TAG, "Unhandled exception in Auth onComplete", e);
                                            if (!isFinishing() && !isDestroyed()) {
                                                Toast.makeText(SignUpActivity.this, "An unexpected error occurred during registration.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        isSignUpInProgress = false;
                        Log.e(LOG_TAG, "Error checking username", task.getException());
                        Toast.makeText(SignUpActivity.this, "Error checking username. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String email, String username) {
        DocumentReference userRef = firebaseFirestore.collection("users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);
        userData.put("username", username);

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    try {
                        Log.d(LOG_TAG, "User data saved to Firestore successfully!");
                        goToFootballPage();
                    } catch (Exception ex) {
                        isSignUpInProgress = false;
                        Log.e(LOG_TAG, "Exception after saving user data or navigating", ex);
                        if (!isFinishing() && !isDestroyed()) {
                            Toast.makeText(SignUpActivity.this, "Error proceeding after saving data.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    isSignUpInProgress = false;
                    if (e instanceof FirebaseFirestoreException) {
                        FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                        Log.e(LOG_TAG, "Firestore error: " + firestoreException.getCode() + " - " + firestoreException.getMessage(), e);
                    } else {
                        Log.e(LOG_TAG, "Error saving user data to Firestore: " + e.getMessage(), e);
                    }
                    if (!isFinishing() && !isDestroyed()) {
                        Toast.makeText(SignUpActivity.this, "Error saving user data. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void back(View view) {
        if (!isSignUpInProgress) {
            finish();
        } else {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "Please wait, registration in progress...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goToFootballPage() {
        try {
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.w(LOG_TAG, "goToFootballPage called but activity is finishing/destroyed.");
                isSignUpInProgress = false;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error navigating to FootballPageActivity: " + e.getMessage(), e);
            isSignUpInProgress = false;
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "Error launching next screen", Toast.LENGTH_SHORT).show();
            }
        }
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
        Log.i(LOG_TAG, "onResume");
        isSignUpInProgress = false;

        ImageView imageView = findViewById(R.id.imageView);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    Animation rotateAnimation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.rotate);
                    if (imageView != null && rotateAnimation != null) {
                        imageView.startAnimation(rotateAnimation);
                    } else {
                        Log.w(LOG_TAG, "ImageView or Animation is null in onResume Handler.");
                    }
                }
            }
        }, 1000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}