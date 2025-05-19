package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditTournamentActivity extends AppCompatActivity {
    private FirebaseUser user;
    private static final String LOG_TAG = EditTournamentActivity.class.getName();
    private FirebaseFirestore db;
    private String tournamentDocumentId = null;

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText descriptionEditText;
    private EditText maxNumberOfTeamsEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_tournament);

        Log.i(LOG_TAG, "onCreate");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            Toast.makeText(this, "Kérlek jelentkezz be a funkció eléréséhez!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Vendég felhasználó vagy nincs bejelentkezve.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        Log.d(LOG_TAG, "Hitelesített felhasználó.");
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxNumberOfTeamsEditText = findViewById(R.id.maxNumberOfTeamsEditText);

        Intent intent = getIntent();
        tournamentDocumentId = intent.getStringExtra("documentId");

        if (tournamentDocumentId != null && !tournamentDocumentId.isEmpty()) {
            Log.d(LOG_TAG, "Tournament Document ID received: " + tournamentDocumentId);
            loadTournamentData(tournamentDocumentId);
        } else {
            Toast.makeText(this, "Nincs megadva torna ID a szerkesztéshez.", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "No documentId extra found in Intent.");
            finish();
            return;
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void loadTournamentData(String documentId) {
        db.collection("tournaments").document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location");
                            String startDate = documentSnapshot.getString("startDate");
                            String endDate = documentSnapshot.getString("endDate");
                            String description = documentSnapshot.getString("description");
                            Long maxTeamsLong = documentSnapshot.getLong("maxNumberOfTeams");
                            String maxNumberOfTeams = (maxTeamsLong != null) ? String.valueOf(maxTeamsLong) : "";

                            nameEditText.setText(name);
                            locationEditText.setText(location);
                            startDateEditText.setText(startDate);
                            endDateEditText.setText(endDate);
                            descriptionEditText.setText(description);
                            maxNumberOfTeamsEditText.setText(maxNumberOfTeams);

                            Log.d(LOG_TAG, "Tournament data loaded successfully.");
                        } else {

                            Toast.makeText(EditTournamentActivity.this, "A torna nem található.", Toast.LENGTH_LONG).show();
                            Log.w(LOG_TAG, "Tournament document with ID " + documentId + " does not exist.");
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditTournamentActivity.this, "Hiba történt a torna adatainak betöltésekor.", Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Error loading tournament data", e);
                        finish();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tournaments_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        if (searchItem != null) {
            searchItem.setVisible(false);
        }

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
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.my_teams) {
            Log.i(LOG_TAG, "Clicked my_teams menu option.");
            Toast.makeText(this, "My Teams funkció még nincs implementálva.", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, MyTeamsActivity.class));
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            startActivity(new Intent(this, MyTournamentsActivity.class));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }   else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void updateTournament(View view) {
        if (tournamentDocumentId == null || tournamentDocumentId.isEmpty()) {
            Toast.makeText(this, "Nincs megadva torna ID a szerkesztéshez.", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Cannot save: tournamentDocumentId is null or empty.");
            return;
        }


        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String maxNumberOfTeamsStr = maxNumberOfTeamsEditText.getText().toString().trim();


        if (name.isEmpty() || location.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || maxNumberOfTeamsStr.isEmpty()) {
            Toast.makeText(this, "Kérlek töltsd ki az összes kötelező mezőt.", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxNumberOfTeams;
        try {
            maxNumberOfTeams = Integer.parseInt(maxNumberOfTeamsStr);
            if (maxNumberOfTeams <= 0) { // További validáció
                Toast.makeText(this, "Maximális csapatok száma pozitív egész szám kell legyen.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen szám a maximális csapatoknál.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (user != null) {
            String uid = user.getUid();
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("username")) {

                            String organiser = documentSnapshot.getString("username");
                            Map<String, Object> tournamentUpdates = new HashMap<>();
                            tournamentUpdates.put("name", name);
                            tournamentUpdates.put("location", location);
                            tournamentUpdates.put("startDate", startDate);
                            tournamentUpdates.put("endDate", endDate);

                            tournamentUpdates.put("organiser", organiser);
                            tournamentUpdates.put("description", description);
                            tournamentUpdates.put("maxNumberOfTeams", maxNumberOfTeams);

                            db.collection("tournaments")
                                    .document(tournamentDocumentId)
                                    .update(tournamentUpdates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(LOG_TAG, "Tournament updated successfully with ID: " + tournamentDocumentId);
                                        Toast.makeText(this, "Torna sikeresen frissítve", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MyTournamentsActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(LOG_TAG, "Error updating tournament", e);
                                        Toast.makeText(this, "Hiba történt a torna frissítésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        } else {

                            Toast.makeText(this, "Nem sikerült lekérdezni a felhasználónevet.", Toast.LENGTH_SHORT).show();
                            Log.w(LOG_TAG, "Username does not exist in user document or user document not found for UID: " + uid);

                        }
                    })
                    .addOnFailureListener(e -> {

                        Log.w(LOG_TAG, "Error getting user document for UID: " + uid, e);
                        Toast.makeText(this, "Hiba történt a felhasználói adatok lekérésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


        } else {

            Toast.makeText(this, "Felhasználó nincs bejelentkezve.", Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, "User not logged in during save attempt.");
        }
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