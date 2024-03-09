package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MedFormFragment extends Fragment {
    ImageView back;
    Button pill, injection, solution, drops, powder, other;
    AddMedicationActivity addMedicationActivity;
    private FirebaseFirestore db;
    String documentId;
    Button[] buttons;
    String[] medForms;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        pill = view.findViewById(R.id.pill);
        injection = view.findViewById(R.id.injection);
        solution = view.findViewById(R.id.solution);
        drops = view.findViewById(R.id.drops);
        powder = view.findViewById(R.id.powder);
        other = view.findViewById(R.id.other);
        db = FirebaseFirestore.getInstance();
        documentId = getArguments().getString("documentId");
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        buttons = new Button[]{pill, injection, solution, drops, powder, other};
        medForms = new String[]{"pill", "injection", "solution", "drops", "powder", "other"};

        for (int i = 0; i < buttons.length; i++) {
            int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMedFormToDB(medForms[j]);
                    addMedicationActivity.showMedFrequencyFragment();
                }
            });
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedFormFragment();
            }
        });



    }
    private void addMedFormToDB(String medForm) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medForm", medForm);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medForm + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medForm + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_form, container, false);
    }
}