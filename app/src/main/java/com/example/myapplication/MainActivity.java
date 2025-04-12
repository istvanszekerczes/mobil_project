package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);

        Log.i(LOG_TAG, "onCreate");

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);


    }

    public void login(View view) {


        String username = usernameEditText.getText().toString();
        Log.i(LOG_TAG, "Logged in succesfully " + username);

        //TODO: Login befejezese

        goToFootballPage();
    }

    public void goToFootballPage(/* registered user data */) {
        Intent intent = new Intent(this, FootballPageActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
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
        editor.putString("username", usernameEditText.getText().toString());
        editor.apply();

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