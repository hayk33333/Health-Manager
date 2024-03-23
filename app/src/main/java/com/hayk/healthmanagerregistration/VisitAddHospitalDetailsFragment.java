package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;


public class VisitAddHospitalDetailsFragment extends Fragment {
    private EditText nameEt, emailEt, phoneNumberEt, addressEt, websiteEt, commentEt;
    private AddVisitActivity addVisitActivity;
    private String documentId;
    private FirebaseFirestore db;
    private ImageView back;
    private Button next;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        documentId = getArguments().getString("documentId");
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        nameEt = view.findViewById(R.id.hospital_name);
        emailEt = view.findViewById(R.id.hospital_email);
        phoneNumberEt = view.findViewById(R.id.hospital_phone);
        addressEt = view.findViewById(R.id.hospital_address);
        websiteEt = view.findViewById(R.id.hospital_web);
        commentEt = view.findViewById(R.id.hospital_additional_comments);
        addVisitActivity = (AddVisitActivity) requireActivity();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.hideVisitHospitalDetailsFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hospitalName = nameEt.getText().toString();
                String hospitalEmail = emailEt.getText().toString();
                String hospitalPhone = phoneNumberEt.getText().toString();
                String hospitalAddress = addressEt.getText().toString();
                String hospitalWebsite = websiteEt.getText().toString();
                String hospitalComment = commentEt.getText().toString();
                addHospitalDetailsToDB(hospitalName, hospitalEmail, hospitalPhone, hospitalAddress, hospitalWebsite, hospitalComment);
                addVisitActivity.hideVisitHospitalDetailsFragment();
            }
        });


    }

    private void addHospitalDetailsToDB(String name, String email, String phone, String addres,
                                      String website, String comment) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        if (name.isEmpty()) {
            visitData.put("hospitalName", null);
        } else {
            visitData.put("hospitalName", name);
        }
        if (email.isEmpty()) {
            visitData.put("hospitalEmail", null);

        } else {
            visitData.put("hospitalEmail", email);

        }
        if (phone.isEmpty()) {
            visitData.put("hospitalPhone", null);
        } else {
            visitData.put("hospitalPhone", phone);
        }
        if (website.isEmpty()) {
            visitData.put("hospitalWebsite", null);
        } else {
            visitData.put("hospitalWebsite", website);
        }
        if (addres.isEmpty()) {
            visitData.put("hospitalAddress", null);
        } else {
            visitData.put("hospitalAddress", addres);
        }
        if (comment.isEmpty()) {
            visitData.put("hospitalComment", null);
        } else {
            visitData.put("hospitalComment", comment);
        }

        visitsCollection
                .document(documentId)
                .update(visitData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_add_hospital_details, container, false);
    }
}