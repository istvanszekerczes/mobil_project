package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getName();
    private FirebaseUser user;
    private TournamentAdapter mAdapter;
    private TextView textViewName, textViewEmail;
    private FirebaseAuth mAuth;
    private static final int SECRET_KEY = 99;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.i(LOG_TAG, "ProfileActivity created");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secretKey != SECRET_KEY) {
            finish();
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        if (user.isAnonymous()) {
            Toast.makeText(this, "Login to access this feature!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Guest user has no profile!.");
            startActivity(new Intent(this, MainActivity.class).putExtra("SECRET_KEY", SECRET_KEY));
            finish();
            return;
        }
        textViewName = findViewById(R.id.textViewNameProfile);
        textViewEmail = findViewById(R.id.textViewEmailProfile2);
        db = FirebaseFirestore.getInstance();
        if (user != null) {
            textViewEmail.setText(user.getEmail());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            loadProfileData();
        }
    }

    private void loadProfileData() {
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    if (username != null) {
                        textViewName.setText(username);
                    } else {
                        Log.w(LOG_TAG, "Username is null in Firestore");
                    }
                } else {
                    Log.w(LOG_TAG, "No such document");
                    textViewName.setText("No profile data found");
                }
            } else {
                Log.e(LOG_TAG, "Error getting document: ", task.getException());
                textViewName.setText("Error loading profile");
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
            return true;
        } else if (id == R.id.my_teams) {
            Log.i(LOG_TAG, "Clicked my_teams menu option.");
            return true;
        } else if (id == R.id.my_tournaments) {
            Log.i(LOG_TAG, "Clicked my_tournaments menu option.");
            startActivity(new Intent(this, MyTournamentsActivity.class));
            return true;
        } else if (id == R.id.home) {
            Log.i(LOG_TAG, "Clicked home menu option.");
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void deleteProfile(View view) {
        if (user != null) {
            final String userEmail = user.getEmail();
            db.collection("users").whereEqualTo("email", userEmail).get().addOnCompleteListener(firestoreTask -> {
                if (firestoreTask.isSuccessful()) {
                    QuerySnapshot querySnapshot = firestoreTask.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        if (documents != null && documents.size() > 0) {
                            documents.get(0).getReference().delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    user.delete().addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            Toast.makeText(this, "Profile deleted.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                            Log.e(LOG_TAG, "Auth Error: ", authTask.getException());
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Failed to delete data.", Toast.LENGTH_SHORT).show();
                                    Log.e(LOG_TAG, "Firestore Error: ", deleteTask.getException());
                                }
                            });
                        }
                    } else {
                        user.delete().addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                Toast.makeText(this, "Profile deleted.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                Log.e(LOG_TAG, "Auth Error: ", authTask.getException());
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Failed to delete profile.", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Firestore Error: ", firestoreTask.getException());
                }
            });
        } else {
            Toast.makeText(this, "No user.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
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
