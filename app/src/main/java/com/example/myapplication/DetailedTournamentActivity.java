package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailedTournamentActivity extends AppCompatActivity {

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<Tournament> tournamentList;
    private TournamentAdapter mAdapter;

    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private int gridNumber = 1;
    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detailed_tournament);

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
            // startActivity(new Intent(this, MyTeamsActivity.class));
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            startActivity(new Intent(this, MyTournamentsActivity.class));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            return true;
        }   else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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