package com.hayk.healthmanagerregistration;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MedSecondDoseTimeFragment extends Fragment {
    ImageView back;
    Button next;
    String documentId;
    private FirebaseFirestore db;
    AddMedicationActivity addMedicationActivity;
    String firstTime = "";
    NumberPicker hour, minute;
    int firstMinute;
    int firstHour;
    int size = 0;
    String[] displayedValues;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        documentId = getArguments().getString("documentId");
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        db = FirebaseFirestore.getInstance();
        hour = view.findViewById(R.id.hour);
        minute = view.findViewById(R.id.minute);
        minute.setMinValue(0);
        minute.setMaxValue(11);
        String[] displayedValues1 = new String[12];
        for (int i = 0; i <= 11; i++) {
            if (i == 0) {
                displayedValues1[i] = "00";
            } else {
                displayedValues1[i] = String.valueOf(i * 5);

            }
        }
        setNumberPickerTime();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourValue = hour.getValue();
                int minuteValue = minute.getValue();
                if (hourValue == firstHour) {
                    addMedSecondTimeToDB(String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5) + 30);

                }

                addMedSecondTimeToDB(String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedSecondDoseFragment();
            }
        });
        hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                try {
                    if (newVal == firstHour) {
                        minute.setMinValue(0);
                        minute.setMaxValue(size-1);
                        minute.setDisplayedValues(displayedValues);
                    } else {
                        minute.setMinValue(0);
                        minute.setMaxValue(11);
                        minute.setDisplayedValues(displayedValues1);

                    }

                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });

    }

    private void addMedSecondTimeToDB(String medSecondTime) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medSecondTime", medSecondTime);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medSecondTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medSecondTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    private void setNumberPickerTime() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Получаем данные из документа
                            firstTime = documentSnapshot.getString("medFirstTime");
                            System.out.println(firstTime);

                            // Помещаем код, зависящий от firstTime, сюда
                            // Например, инициализация NumberPicker
                            initializeNumberPicker();
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

    private void initializeNumberPicker() {
        String[] parts = firstTime.split(":");

        if (parts.length >= 2) {
            String hourStr = parts[0];
            firstHour = Integer.parseInt(hourStr);

            String minuteStr = parts[1];
            firstMinute = Integer.parseInt(minuteStr);

            hour.setMinValue(firstHour);
            hour.setMaxValue(23);
            for (int i = 0; i < 60; i += 5) {
                if (i > firstMinute) {
                    size++;
                }
            }


            displayedValues = new String[size];
            minute.setMinValue(1);
            minute.setMaxValue(size);
            int j = size;
            for (int i = 0; i < size; i++) {
                j--;
                displayedValues[i] = String.valueOf((11 - j) * 5); // Adjust the index to start from size
            }


            minute.setDisplayedValues(displayedValues);
            hour.invalidate();
            minute.invalidate();


        } else {
            Toast.makeText(addMedicationActivity, "Ошибка при разделении строки времени", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_second_dose_time, container, false);
    }
}