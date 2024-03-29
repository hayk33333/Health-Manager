package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MedicationsFragment extends Fragment {
    private Button addMedButton;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMedButton = view.findViewById(R.id.add_med_button);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserMeds();

        addMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingMedication();
            }
        });

    }

    private void getUserMeds() {
        db.collection("users").document(currentUser.getUid()).collection("userMeds")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> userMedsNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Получаем имя документа
                            String docName = document.getId();
                            userMedsNames.add(docName);
                        }

                        // Здесь вы можете использовать список userMedsNames с именами документов
                        // например, вывести их в лог или выполнить другие операции
                        for (String name : userMedsNames) {
                            System.out.println("Document name: " + name);
                        }
                    }
                });
    }

    private void startAddingMedication() {
        Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medications, container, false);
    }
}