package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    MedicationsFragment medicationsFragment;
    VisitsFragment visitsFragment;
    OptionsFragment optionsFragment;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Intents intents = new Intents(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()){
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

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
        if (item.getItemId() == R.id.home){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, homeFragment)
                    .commit();
        }
        else if (item.getItemId() == R.id.medications){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, medicationsFragment)
                    .commit();
        }
        else if (item.getItemId() == R.id.calendar){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, visitsFragment)
                    .commit();
        }
        else if (item.getItemId() == R.id.options){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, optionsFragment)
                    .commit();
        }
        return true;
    }
}
