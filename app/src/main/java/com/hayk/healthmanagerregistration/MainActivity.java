package com.hayk.healthmanagerregistration;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;

import AddMedFragments.AlarmDates;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private MedicationsFragment medicationsFragment;
    private VisitsFragment visitsFragment;
    private OptionsFragment optionsFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Intents intents;
    private static final String PREFS_NAME = "language_prefs";
    private static final String LANGUAGE_KEY = "language";
    private String action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intents = new Intents(this);
        String language = getSavedLanguagePreference();
        setLocale(language, false);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        action = getIntent().getAction();

        if (currentUser == null || !currentUser.isEmailVerified()) {
            if (currentUser != null) {
                String email = currentUser.getEmail();
                if (email != null && !email.equals("sictst1@gmail.com")) {
                    firebaseAuth.signOut();
                    redirectToLogin();
                }
            } else {
                redirectToLogin();
            }
        }


        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        homeFragment = new HomeFragment();
        medicationsFragment = new MedicationsFragment();
        visitsFragment = new VisitsFragment();
        optionsFragment = new OptionsFragment();

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        if (action != null && action.equals("OPTIONS_FRAGMENT")){
            bottomNavigationView.setSelectedItemId(R.id.options);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, optionsFragment)
                    .commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, homeFragment)
                    .commit();
        } else if (item.getItemId() == R.id.medications) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, medicationsFragment)
                    .commit();
        } else if (item.getItemId() == R.id.calendar) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, visitsFragment)
                    .commit();
        } else if (item.getItemId() == R.id.options) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, optionsFragment)
                    .commit();
        }
        return true;
    }

    private void redirectToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    private void saveLanguagePreference(String languageCode) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(LANGUAGE_KEY, languageCode)
                .apply();
    }

    private String getSavedLanguagePreference() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "en");
    }
    private void setLocale(String languageCode, boolean shouldRestartActivity) {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        if (!currentLanguage.equals(languageCode)) {
            LanguageManager.setLocale(this, new Locale(languageCode));
            saveLanguagePreference(languageCode);

            if (shouldRestartActivity) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        String language = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "en");
        super.attachBaseContext(LanguageManager.updateBaseContextLocale(newBase, new Locale(language)));
    }
}
