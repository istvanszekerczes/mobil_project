package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class CreateTournamentActivity extends AppCompatActivity {
    private FirebaseUser user;
    private TournamentAdapter mAdapter;
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private FirebaseFirestore db;
    private EditText nameEditText;
    private EditText locationEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText descriptionEditText;
    private EditText maxNumberOfTeamsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);
        Log.i(LOG_TAG, "onCreate");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        if (user.isAnonymous()) {
            Toast.makeText(this, "Login to access this feature!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Guest user has no profile!.");
            startActivity(new Intent(this, MainActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            finish();
            return;
        }
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user.");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        db = FirebaseFirestore.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxNumberOfTeamsEditText = findViewById(R.id.maxNumberOfTeamsEditText);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                Log.i(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
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
            return true;
        } else if (id == R.id.my_tournaments) {
            startActivity(new Intent(this, MyTournamentsActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void createTournament(View view) {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String maxNumberOfTeamsStr = maxNumberOfTeamsEditText.getText().toString();
        if (name.isEmpty() || location.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || maxNumberOfTeamsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            Toast.makeText(this, "Invalid date format. Please use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }
        int maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsStr);
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                    String organiser = documentSnapshot.getString("username");
                    Map<String, Object> tournament = new HashMap<>();
                    tournament.put("name", name);
                    tournament.put("location", location);
                    tournament.put("startDate", startDate);
                    tournament.put("endDate", endDate);
                    tournament.put("organiser", organiser);
                    tournament.put("description", description);
                    tournament.put("maxNumberOfTeams", maxNumberOfTeams);
                    db.collection("tournaments").add(tournament).addOnSuccessListener(documentReference -> {
                        Log.d(LOG_TAG, "Tournament added with ID: " + documentReference.getId());
                        Toast.makeText(this, "Tournament created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MyTournamentsActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Log.w(LOG_TAG, "Error adding tournament", e);
                        Toast.makeText(this, "Failed to create tournament: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(this, "Could not retrieve username", Toast.LENGTH_SHORT).show();
                    Log.w(LOG_TAG, "Username does not exist in user document");
                }
            }).addOnFailureListener(e -> {
                Log.w(LOG_TAG, "Error getting user document", e);
                Toast.makeText(this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, "User not logged in");
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
