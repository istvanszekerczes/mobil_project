package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TournamentDetailsActivity extends AppCompatActivity {

    private TextView detailName;
    private TextView detailOrganiser;
    private TextView detailLocation;
    private TextView detailStartDate;
    private TextView detailEndDate;
    private TextView detailMaxTeams;
    private TextView detailDescription;

    private FirebaseUser user;
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = TournamentDetailsActivity.class.getName();
    private static final int SECRET_KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_details);

        Log.i(LOG_TAG, "onCreate");

        // Initialize TextViews
        detailName = findViewById(R.id.detailName);
        detailOrganiser = findViewById(R.id.detailOrganiser);
        detailLocation = findViewById(R.id.detailLocation);
        detailStartDate = findViewById(R.id.detailStartDate);
        detailEndDate = findViewById(R.id.detailEndDate);
        detailMaxTeams = findViewById(R.id.detailMaxTeams);
        detailDescription = findViewById(R.id.detailDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user.");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Make sure the user can't go back without logging in
            return;
        }
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String documentId = getIntent().getStringExtra("documentId");

        if (documentId != null) {
            loadTournamentDetails(documentId);
        } else {
            Log.w(LOG_TAG, "No document ID received!");
            Toast.makeText(this, "Tournament ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no ID is available
        }
    }

    private void loadTournamentDetails(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tournamentRef = db.collection("tournaments").document(documentId);

        tournamentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(LOG_TAG, "DocumentSnapshot data: " + document.getData());
                    Tournament tournament = document.toObject(Tournament.class);
                    if (tournament != null) {
                        displayTournamentDetails(tournament);
                    } else {
                        Log.w(LOG_TAG, "Error converting document to Tournament object");
                        Toast.makeText(this, "Error loading tournament details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(LOG_TAG, "No such document");
                    Toast.makeText(this, "Tournament not found", Toast.LENGTH_SHORT).show();
                    finish(); // Close if document doesn't exist
                }
            } else {
                Log.d(LOG_TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Error loading tournament details", Toast.LENGTH_SHORT).show();
                finish(); // Close on failure
            }
        });
    }

    private void displayTournamentDetails(Tournament tournament) {
        detailName.setText(tournament.getName());

        // Most közvetlenül a String-et állítjuk be
        if (tournament.getOrganiser() != null && !tournament.getOrganiser().isEmpty()) {
            detailOrganiser.setText(tournament.getOrganiser());
        } else {
            detailOrganiser.setText("N/A");
        }

        detailLocation.setText(tournament.getLocation());
        detailStartDate.setText(tournament.getStartDate());
        detailEndDate.setText(tournament.getEndDate());
        detailMaxTeams.setText(String.valueOf(tournament.getMaxNumberOfTeams()));
        detailDescription.setText(tournament.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tournaments_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // This is for the main tournament list, not relevant here
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            Log.i(LOG_TAG, "Clicked logout menu option.");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            Log.i(LOG_TAG, "Logged out successfully!");
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.profile) {
            Log.i(LOG_TAG, "Clicked profile menu option.");
            startActivity(new Intent(this, ProfileActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            return true;
        } else if (id == R.id.my_teams) {
            Log.i(LOG_TAG, "Clicked my_teams menu option.");
            // startActivity(new Intent(this, MyTeamsActivity.class));  // You need to create MyTeamsActivity
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            startActivity(new Intent(this, MyTournamentsActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class).putExtra("LOG_TAG", LOG_TAG));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Remove search bar in details view
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        if (searchItem != null) {
            searchItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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