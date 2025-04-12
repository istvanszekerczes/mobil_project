package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FootballPageActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = FootballPageActivity.class.getPackage().toString();
    private static final String LOG_TAG = FootballPageActivity.class.getName();
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_page);

        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secretKey != SECRET_KEY) {
            finish();
        }

        Log.i(LOG_TAG, "onCreate");

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String username =  sharedPreferences.getString("username", "");



    }

    public void exit(View view) {
        finishAffinity();
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
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