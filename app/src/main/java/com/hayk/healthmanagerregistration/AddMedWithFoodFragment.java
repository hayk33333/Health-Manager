package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class AddMedWithFoodFragment extends Fragment {
    ImageView back;
    AddMedicationActivity addMedicationActivity;
    String documentId;
    FirebaseFirestore db;
    Button afterEating, beforeEating, fromEating;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        back = view.findViewById(R.id.back);
        db = FirebaseFirestore.getInstance();
        afterEating = view.findViewById(R.id.after_eating);
        beforeEating = view.findViewById(R.id.before_eating);
        fromEating = view.findViewById(R.id.from_eating);
        documentId = getArguments().getString("documentId");
        afterEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedWithFoodToDB("afterEating");
                addMedicationActivity.hideAddMedWithFoodFragment();
                MedAdditionalInformationFragment medAdditionalInformationFragment = new MedAdditionalInformationFragment();


            }
        });
        beforeEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedWithFoodToDB("beforeEating");
                addMedicationActivity.hideAddMedWithFoodFragment();
            }
        });
        fromEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedWithFoodToDB("fromEating");
                addMedicationActivity.hideAddMedWithFoodFragment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideAddMedWithFoodFragment();
            }
        });


    }
    private void addMedWithFoodToDB(String medWithFood) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medWithFood", medWithFood);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medWithFood + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medWithFood + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_med_with_food, container, false);
    }
}