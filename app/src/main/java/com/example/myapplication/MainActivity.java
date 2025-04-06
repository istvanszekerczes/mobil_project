package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText usernameET;
    EditText passwordET;
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = findViewById(R.id.editTextUsername);
        passwordET = findViewById(R.id.editTextPassword);
    }

    public void login(View view) {


        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        Log.i(LOG_TAG, "Logged in succesfully " + username);

        //TODO: Login befejezese
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);

    }
}