package com.hayk.healthmanagerregistration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MedTimeFragment extends Fragment {
    ImageView back;
    Button next;
    String documentId;
    FirebaseFirestore db;
    NumberPicker hour;
    NumberPicker minute;
    AddMedicationActivity addMedicationActivity;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        hour = view.findViewById(R.id.hour);
        hour.setMinValue(0);
        hour.setMaxValue(23);
        minute = view.findViewById(R.id.minute);
        minute.setMinValue(0);
        minute.setMaxValue(11);
        String[] displayedValues = new String[12];
        for (int i = 0; i <= 11; i++) {
            if (i == 0) {
                displayedValues[i] = "00";
            } else {
                displayedValues[i] = String.valueOf(i * 5);
            }
        }
        hour.setValue(8);
        minute.setDisplayedValues(displayedValues);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourValue = hour.getValue();
                int minuteValue = minute.getValue();

                addMedTimeToDB(String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5));
                addMedicationActivity.showMedAdditionalInformationFragment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedTimeFragment();
            }
        });

    }
    private void addMedTimeToDB(String medTime) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medTime", medTime);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_med_time, container, false);
    }
}