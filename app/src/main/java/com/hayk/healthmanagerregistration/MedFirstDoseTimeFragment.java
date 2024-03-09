package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MedFirstDoseTimeFragment extends Fragment {

    ImageView back;
    Button next;
    String documentId;
    private FirebaseFirestore db;
    AddMedicationActivity addMedicationActivity;
    String medFrequencyInDay;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        documentId = getArguments().getString("documentId");
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        db = FirebaseFirestore.getInstance();
        getMedFrequencyInDayFromDb();
        NumberPicker hour = view.findViewById(R.id.hour);
        hour.setMinValue(0);
        hour.setMaxValue(23);
        NumberPicker minute = view.findViewById(R.id.minute);
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

                addMedFirstTimeToDB(String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5));
                try {
                    if (medFrequencyInDay.equals("twiceDay")) {
                        addMedicationActivity.showMedSecondDoseTimeFragment();
                    } else if (medFrequencyInDay.equals("moreTimes")){
                        addMedicationActivity.showMedReviewRemindersFragment();
                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedFirstDoseFragment();
            }
        });


    }

    private void addMedFirstTimeToDB(String medFirstTime) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medFirstTime", medFirstTime);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medFirstTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medFirstTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    private void getMedFrequencyInDayFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");
        final String[] frequency = new String[1];
        frequency[0] = "";

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Получаем данные из документа
                            frequency[0] = documentSnapshot.getString("medFrequencyInDay");
                            medFrequencyInDay = frequency[0];
                        } else {
                            System.out.println("med does not exists");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.printf("error to read from db");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_first_dose_time, container, false);
    }
}