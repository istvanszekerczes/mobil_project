package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.myapplication.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<Tournament> tournamentList;
    private TournamentAdapter mAdapter;

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.i(LOG_TAG, "onCreate");

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user.");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        tournamentList = new ArrayList<>();
        mAdapter = new TournamentAdapter(this, tournamentList);
        recyclerView.setAdapter(mAdapter);

        initializeData();

        mAdapter.setOnItemClickListener(new TournamentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Tournament clickedTournament = tournamentList.get(position);
                Log.d(LOG_TAG, "Details gomb megnyomva a pozíción: " + position + ", Név: " + clickedTournament.getName());
                Toast.makeText(MainActivity.this, "Részletek: " + clickedTournament.getName(), Toast.LENGTH_SHORT).show();

                // Példa: Indíts egy új activity-t a részletek megjelenítéséhez
                Intent detailsIntent = new Intent(MainActivity.this, TournamentDetailsActivity.class);
                detailsIntent.putExtra("documentId", clickedTournament.getDocumentId());
                detailsIntent.putExtra("LOG_TAG", LOG_TAG);
                startActivity(detailsIntent);
            }
        });

    }

    private void initializeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tournaments")
                .orderBy("startDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(LOG_TAG, "Document snapshot data: " + document.getData().toString());
                                tournamentList.add(new Tournament(document.getId() ,document.getString("name"), document.getString("location"),
                                        document.getString("startDate"), document.getString("endDate"), document.getString("description")));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.w(LOG_TAG, "Error getting documents.", task.getException());
                    }

                });
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
            // startActivity(new Intent(this, MyTeamsActivity.class));
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            startActivity(new Intent(this, MyTournamentsActivity.class));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            return true;
        }   else {
            return super.onOptionsItemSelected(item);
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

