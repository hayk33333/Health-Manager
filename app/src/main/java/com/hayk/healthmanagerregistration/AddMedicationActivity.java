package com.hayk.healthmanagerregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMedicationActivity extends AppCompatActivity {
    TextView message;
    ImageView icon;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        message = findViewById(R.id.med_activity_message);
        icon = findViewById(R.id.icon);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        showMedNameFragment();
        db = FirebaseFirestore.getInstance();
//        CollectionReference usersCollection = db.collection("users");
//
//        String userId = user.getUid();
//
//        Map<String, Object> medsFolder = new HashMap<>();
//        usersCollection.document(userId).collection("meds").document("medsFolder").set(medsFolder)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        System.out.println("Папка 'meds' успешно создана для пользователя с ID:");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("Ошибка при создании папки 'meds' для пользователя с ID: ");
//                    }
//                });
    }


    public void showMedNameFragment() {
        progressBar.setProgress(10);
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        MedNameFragment medNameFragment = new MedNameFragment();
        icon.setImageResource(R.drawable.med_interrogative);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medNameFragment)
                .commit();
    }

    public void showMedFormFragment() {
        progressBar.setProgress(20);
        icon.setImageResource(R.drawable.med_form);
        message.setText(R.string.what_form_is_the_med);
        MedFormFragment medFormFragment = new MedFormFragment();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFormFragment)
                .commit();
    }
    public void showMedFrequencyFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedFrequencyFragment medFrequencyFragment = new MedFrequencyFragment();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFrequencyFragment)
                .commit();
    }
    public void showMedFrequencyInDayFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedFrequencyInDayFragment medFrequencyInDayFragment = new MedFrequencyInDayFragment();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFrequencyInDayFragment)
                .commit();
    }
    public void showDaysOfWeekFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.on_which_days_s_do_you_need_to_take_the_med);
        DaysOfWeekFragment daysOfWeekFragment = new DaysOfWeekFragment();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, daysOfWeekFragment)
                .commit();
    }

    public void hideMedFormFragment() {
        progressBar.setProgress(10);
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        MedFormFragment medFormFragment = (MedFormFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFormFragment != null && !medFormFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFormFragment).commit();
        }

    }

    public void hideMedFrequencyFragment() {
        progressBar.setProgress(20);
        icon.setImageResource(R.drawable.med_form);
        message.setText(R.string.what_form_is_the_med);
        MedFrequencyFragment medFrequencyFragment = (MedFrequencyFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFrequencyFragment != null && !medFrequencyFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFrequencyFragment).commit();
        }
    }

    public void hideMedFrequencyInDayFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedFrequencyInDayFragment medFrequencyInDayFragment = (MedFrequencyInDayFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFrequencyInDayFragment != null && !medFrequencyInDayFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFrequencyInDayFragment).commit();
        }
    }
    public void hideDaysOfWeekFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        DaysOfWeekFragment daysOfWeekFragment = (DaysOfWeekFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (daysOfWeekFragment != null && !daysOfWeekFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(daysOfWeekFragment).commit();
        }
    }

}