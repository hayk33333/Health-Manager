package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MedNameFragment extends Fragment {
    Button next;
    EditText medNameEt;
    Drawable red_et_background;
    Drawable et_background;
    TextView message;
    ImageView back;
    private FirebaseFirestore db;
    String documentId;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        next = view.findViewById(R.id.next);
        medNameEt = view.findViewById(R.id.med_name_et);
        message = view.findViewById(R.id.message);
        back = view.findViewById(R.id.back);
        db = FirebaseFirestore.getInstance();
        documentId = getArguments().getString("documentId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        medNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                medNameEt.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                message.setText("");
                medNameEt.setBackground(et_background);
                String medName = medNameEt.getText().toString();
                if (medName.isEmpty()) {
                    message.setText("Please enter the med name");
                    medNameEt.setBackground(red_et_background);

                } else {
                    addMedNameToDB(medName);

                    AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                    addMedicationActivity.showMedFormFragment();
                }
            }
        });

    }


    private void addMedNameToDB(String medName) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medName", medName);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medName + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medName + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_name, container, false);
    }
}