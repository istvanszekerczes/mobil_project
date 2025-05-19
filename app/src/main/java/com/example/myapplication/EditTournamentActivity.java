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
    private static final String LOG_TAG = EditTournamentActivity.class.getName(); // LOG_TAG javítása
    private FirebaseFirestore db;
    private String tournamentDocumentId = null; // Itt tároljuk a szerkesztendő dokumentum ID-ját

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

        // User autentikáció ellenőrzése (ez jó volt)
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            Toast.makeText(this, "Kérlek jelentkezz be a funkció eléréséhez!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Vendég felhasználó vagy nincs bejelentkezve.");
            // Vissza a főképernyőre vagy bejelentkezésre
            startActivity(new Intent(this, MainActivity.class)); // Vagy LoginActivity
            finish();
            return;
        }

        Log.d(LOG_TAG, "Hitelesített felhasználó.");
        db = FirebaseFirestore.getInstance();

        // EditText mezők inicializálása (javítva a hozzárendelés)
        nameEditText = findViewById(R.id.nameEditText); // Feltételezve, hogy van ilyen ID a layoutban
        locationEditText = findViewById(R.id.locationEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxNumberOfTeamsEditText = findViewById(R.id.maxNumberOfTeamsEditText); // Feltételezve, hogy van ilyen ID a layoutban

        // Intent-ből lekérdezzük a dokumentum ID-t
        Intent intent = getIntent();
        tournamentDocumentId = intent.getStringExtra("documentId");

        // Ha van dokumentum ID, akkor lekérdezzük az adatokat és feltöltjük a mezőket
        if (tournamentDocumentId != null && !tournamentDocumentId.isEmpty()) {
            Log.d(LOG_TAG, "Tournament Document ID received: " + tournamentDocumentId);
            loadTournamentData(tournamentDocumentId);
        } else {
            Toast.makeText(this, "Nincs megadva torna ID a szerkesztéshez.", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "No documentId extra found in Intent.");
            finish(); // Bezárjuk az Activity-t
            return; // Fontos, hogy kilépjünk, ne próbálkozzunk adatlekérés nélkül
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    // Metódus a torna adatainak lekérésére a Firestore-ból
    private void loadTournamentData(String documentId) {
        db.collection("tournaments").document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Adatok sikeresen lekérdezve, töltsük fel a mezőket
                            String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location");
                            String startDate = documentSnapshot.getString("startDate");
                            String endDate = documentSnapshot.getString("endDate");
                            String description = documentSnapshot.getString("description");
                            // maxNumberOfTeams Long-ként van tárolva, String-gé alakítjuk
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
                            // A dokumentum nem létezik a megadott ID-val
                            Toast.makeText(EditTournamentActivity.this, "A torna nem található.", Toast.LENGTH_LONG).show();
                            Log.w(LOG_TAG, "Tournament document with ID " + documentId + " does not exist.");
                            finish(); // Bezárjuk az Activity-t, ha nem található
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hiba történt a lekérdezés közben
                        Toast.makeText(EditTournamentActivity.this, "Hiba történt a torna adatainak betöltésekor.", Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Error loading tournament data", e);
                        finish(); // Bezárjuk az Activity-t hiba esetén
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // A menü létrehozása (ha szükségesek a menüpontok)
        getMenuInflater().inflate(R.menu.tournaments_menu, menu);
        // A keresősáv valószínűleg nem szükséges a szerkesztő nézetben,
        // de ha igen, győződj meg róla, hogy az mAdapter inicializálva van.
        // Jelen kódban az mAdapter nincs inicializálva, ezért a keresősáv hibát okozhat.
        // A search_bar és a filter logikája ki is törölhető innen.
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        if (searchItem != null) {
            searchItem.setVisible(false); // Elrejtjük a keresőt a szerkesztő nézetben
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Menüpontok kezelése (ez jó volt, csak a MyTeamsActivity hiányzik)
        int id = item.getItemId();

        if (id == R.id.logout) {
            Log.i(LOG_TAG, "Clicked logout menu option.");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            Log.i(LOG_TAG, "Logged out successfully!");
            startActivity(intent);
            finish(); // Bezárjuk ezt az Activityt is kijelentkezéskor
            return true;
        } else if (id == R.id.profile) {
            Log.i(LOG_TAG, "Clicked profile menu option.");
            // SECRET_KEY használata opcionális, ha kell
            startActivity(new Intent(this, ProfileActivity.class)); //.putExtra("SECRET_KEY", SECRET_KEY));
            return true;
        } else if (id == R.id.my_teams) {
            Log.i(LOG_TAG, "Clicked my_teams menu option.");
            Toast.makeText(this, "My Teams funkció még nincs implementálva.", Toast.LENGTH_SHORT).show(); // Jelzés, ha nincs kész
            // startActivity(new Intent(this, MyTeamsActivity.class));
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            // SECRET_KEY használata opcionális, ha kell
            startActivity(new Intent(this, MyTournamentsActivity.class)); // .putExtra("SECRET_KEY", SECRET_KEY));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class));
            // Esetleg finish(), ha nem akarod, hogy vissza lehessen jönni ide a főképernyőről
            return true;
        }   else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void updateTournament(View view) {
        // Ellenőrizzük, hogy van-e dokumentum ID - ha nincs, akkor nincs mit frissíteni
        if (tournamentDocumentId == null || tournamentDocumentId.isEmpty()) {
            Toast.makeText(this, "Nincs megadva torna ID a szerkesztéshez.", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Cannot save: tournamentDocumentId is null or empty.");
            return; // Kilépünk, ha nincs ID
        }


        String name = nameEditText.getText().toString().trim(); // Trim() a felesleges szóközök eltávolítására
        String location = locationEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String maxNumberOfTeamsStr = maxNumberOfTeamsEditText.getText().toString().trim();

        // Validáció
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

            // Lekérdezzük az aktuális felhasználó nevét, hogy beállítsuk szervezőnek (ha szükséges)
            // Megjegyzés: ha a szervező nevét nem akarjuk módosítani szerkesztéskor,
            // ezt a részt ki lehet hagyni, és csak a többi mezőt frissíteni.
            // Ha módosítható a szervező neve, vagy ha csak beállítjuk, ha még nincs,
            // akkor ez a rész maradhat. Most megtartjuk, és feltételezzük, hogy a bejelentkezett user a szervező.
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
                            // A szervező nevének frissítése opcionális szerkesztéskor
                            tournamentUpdates.put("organiser", organiser); // Frissítjük a szervezőt az aktuális user nevére
                            tournamentUpdates.put("description", description);
                            tournamentUpdates.put("maxNumberOfTeams", maxNumberOfTeams);

                            // Itt történik a FRISSÍTÉS az ADD helyett
                            db.collection("tournaments")
                                    .document(tournamentDocumentId) // A meglévő dokumentum ID-ját használjuk
                                    .update(tournamentUpdates) // update() metódus
                                    .addOnSuccessListener(aVoid -> { // update() nem ad vissza DocumentReference-t
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
                            // Hiba, ha nem sikerül lekérdezni a felhasználónevet
                            Toast.makeText(this, "Nem sikerült lekérdezni a felhasználónevet.", Toast.LENGTH_SHORT).show();
                            Log.w(LOG_TAG, "Username does not exist in user document or user document not found for UID: " + uid);
                            // Esetleg finish() itt is, ha a username nélkül nem lehet menteni
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Hiba a felhasználói dokumentum lekérése közben
                        Log.w(LOG_TAG, "Error getting user document for UID: " + uid, e);
                        Toast.makeText(this, "Hiba történt a felhasználói adatok lekérésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


        } else {
            // Ezt a checket fentebb már megtettük, de itt is lehet hagyni biztonsági okból
            Toast.makeText(this, "Felhasználó nincs bejelentkezve.", Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, "User not logged in during save attempt.");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Ez a metódus hívódik meg a menü megjelenítése előtt.
        // Itt lehet menüpontokat dinamikusan módosítani (pl. elrejteni/megjeleníteni).
        return super.onPrepareOptionsMenu(menu);
    }

    // Az Activity életciklus metódusok jók, csak a LOG_TAG-et javítottuk bennük.
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