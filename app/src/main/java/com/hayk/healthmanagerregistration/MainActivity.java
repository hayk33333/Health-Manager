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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intents = new Intents(this);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class); // Замените на свой класс, который обрабатывает оповещения
//        for (int i = 0; i < 101; i++) {
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast((Context) this, i, intent, PendingIntent.FLAG_IMMUTABLE);
//
//            // Если PendingIntent не равен null, значит, оповещение существует, и мы его отменяем
//            if (pendingIntent != null) {
//                alarmManager.cancel(pendingIntent);
//                pendingIntent.cancel();
//            }
//        }
//        Toast.makeText(this, "jnj", Toast.LENGTH_SHORT).show();


        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            firebaseAuth.signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
        //  AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
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
}
