package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FootballPageActivity extends AppCompatActivity {
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<Tournament> tournamentList;
    private TournamentAdapter mAdapter;

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = FootballPageActivity.class.getPackage().toString();
    private static final String LOG_TAG = FootballPageActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_page);

        Log.i(LOG_TAG, "onCreate");

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user.");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user.");
            finish();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        tournamentList = new ArrayList<>();
        mAdapter = new TournamentAdapter(this, tournamentList);
        recyclerView.setAdapter(mAdapter);

        initializeData();

    }

    private void initializeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tournaments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> names = new ArrayList<>();
                        List<String> locations = new ArrayList<>();
                        List<String> startDates = new ArrayList<>();
                        List<String> endDates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            tournamentList.add(new Tournament(document.getString("name"), document.getString("location"),
                                    document.getString("startDate"), document.getString("endDate")));
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(LOG_TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void exit(View view) {
        finishAffinity();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class);

        Log.i(LOG_TAG, "Logged out successfully!");

        startActivity(intent);
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