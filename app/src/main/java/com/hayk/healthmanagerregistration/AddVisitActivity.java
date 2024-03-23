package com.hayk.healthmanagerregistration;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class AddVisitActivity extends AppCompatActivity {
    private TextView message;
    private ImageView icon;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit);
        message = findViewById(R.id.visit_activity_message);
        icon = findViewById(R.id.icon);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        createMedsCollection();
        showVisitNameFragment();



    }
    public void createMedsCollection() {
        CollectionReference medsCollection = db.collection("visits");

        DocumentReference newDocumentRef = medsCollection.document();
        documentId = newDocumentRef.getId();

        newDocumentRef.set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Коллекция 'meds' успешно создана! Документ ID: " + documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при создании коллекции 'meds': " + e.getMessage());
                    }
                });
    }
    public void showVisitNameFragment() {
        icon.setImageResource(R.drawable.visit_name_icon);
        message.setText(R.string.what_would_you_like_to_name_the_visit);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitNameFragment visitNameFragment = new VisitNameFragment();
        visitNameFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitNameFragment)
                .commit();
    }


    public void showVisitChooseDayFragment() {
        icon.setImageResource(R.drawable.visit_calendar_icon);
        message.setText(R.string.when_should_you_go_for_your_visit);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitChooseDayFragment visitChooseDayFragment = new VisitChooseDayFragment();
        visitChooseDayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitChooseDayFragment)
                .commit();
    }
    public void showVisitChooseTimeFragment() {
        icon.setImageResource(R.drawable.visit_time_icon);
        message.setText(R.string.what_time_should_you_go_for_your_visit);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitChooseTimeFragment visitChooseTimeFragment = new VisitChooseTimeFragment();
        visitChooseTimeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitChooseTimeFragment)
                .commit();
    }
    public void showVisitAdditionalInfoFragment() {
        icon.setImageResource(R.drawable.visit_addinitional_info_icon);
        message.setText(R.string.what_also_would_you_like_to_add);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitAdditionalInfoFragment visitAdditionalInfoFragment = new VisitAdditionalInfoFragment();
        visitAdditionalInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitAdditionalInfoFragment)
                .commit();
    }
    public void showVisitAddDoctorDetailsFragment() {
        icon.setImageResource(R.drawable.doctor_icon);
        message.setText(R.string.add_doctor_details);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitAddDoctorDetailsFragment visitAddDoctorDetailsFragment = new VisitAddDoctorDetailsFragment();
        visitAddDoctorDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitAddDoctorDetailsFragment)
                .commit();
    }
    public void showVisitAddHospitalDetailsFragment() {
        icon.setImageResource(R.drawable.hospital_img);
        message.setText(R.string.add_hospital_details);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitAddHospitalDetailsFragment visitAddHospitalDetailsFragment = new VisitAddHospitalDetailsFragment();
        visitAddHospitalDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitAddHospitalDetailsFragment)
                .commit();
    }
    public void showVisitAddCommentFragment() {
        icon.setImageResource(R.drawable.visit_name_icon);
        message.setText(R.string.add_your_visit_additional_comment);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        VisitAddCommentFragment visitAddCommentFragment = new VisitAddCommentFragment();
        visitAddCommentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, visitAddCommentFragment)
                .commit();
    }
    public void hideVisitChooseDayFragment() {
        icon.setImageResource(R.drawable.visit_name_icon);
        message.setText(R.string.what_would_you_like_to_name_the_visit);
        VisitChooseDayFragment visitChooseDayFragment = (VisitChooseDayFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitChooseDayFragment != null && !visitChooseDayFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitChooseDayFragment).commit();
        }

    }
    public void hideVisitChooseTimeFragment() {
        icon.setImageResource(R.drawable.visit_calendar_icon);
        message.setText(R.string.when_should_you_go_for_your_visit);
        VisitChooseTimeFragment visitChooseTimeFragment = (VisitChooseTimeFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitChooseTimeFragment != null && !visitChooseTimeFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitChooseTimeFragment).commit();
        }

    }
    public void hideVisitAdditionalInfoFragment() {
        icon.setImageResource(R.drawable.visit_time_icon);
        message.setText(R.string.what_time_should_you_go_for_your_visit);
        VisitAdditionalInfoFragment visitAdditionalInfoFragment = (VisitAdditionalInfoFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitAdditionalInfoFragment != null && !visitAdditionalInfoFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitAdditionalInfoFragment).commit();
        }

    }
    public void hideVisitDoctorDetailsFragment() {
        icon.setImageResource(R.drawable.visit_addinitional_info_icon);
        message.setText(R.string.what_also_would_you_like_to_add);
        VisitAddDoctorDetailsFragment visitAddDoctorDetailsFragment = (VisitAddDoctorDetailsFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitAddDoctorDetailsFragment != null && !visitAddDoctorDetailsFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitAddDoctorDetailsFragment).commit();
        }

    }
    public void hideVisitHospitalDetailsFragment() {
        icon.setImageResource(R.drawable.visit_addinitional_info_icon);
        message.setText(R.string.what_also_would_you_like_to_add);
        VisitAddHospitalDetailsFragment visitAddHospitalDetailsFragment = (VisitAddHospitalDetailsFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitAddHospitalDetailsFragment != null && !visitAddHospitalDetailsFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitAddHospitalDetailsFragment).commit();
        }

    }
    public void hideVisitCommentFragment() {
        icon.setImageResource(R.drawable.visit_addinitional_info_icon);
        message.setText(R.string.what_also_would_you_like_to_add);
        VisitAddCommentFragment visitAddCommentFragment = (VisitAddCommentFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (visitAddCommentFragment != null && !visitAddCommentFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(visitAddCommentFragment).commit();
        }

    }


}