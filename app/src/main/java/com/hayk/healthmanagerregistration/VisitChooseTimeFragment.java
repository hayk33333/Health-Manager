package com.hayk.healthmanagerregistration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitChooseTimeFragment extends Fragment {
    private ImageView back;
    private Button next;
    private String documentId;
    private FirebaseFirestore db;
    private NumberPicker hour;
    private NumberPicker minute;
    private AddVisitActivity addVisitActivity;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        addVisitActivity = (AddVisitActivity) requireActivity();
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
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
                String time = String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5);
                addVisitTimeToDB(time);
                addVisitActivity.showVisitAdditionalInfoFragment();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.hideVisitChooseTimeFragment();
            }
        });
    }
    private void addVisitTimeToDB(String visitTime) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        visitData.put("visitTime", visitTime);

        visitsCollection
                .document(documentId)
                .update(visitData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + visitTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + visitTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_choose_time, container, false);
    }
}