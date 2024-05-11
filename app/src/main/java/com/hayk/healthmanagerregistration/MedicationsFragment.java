package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import AddMedFragments.CustomAdapter;
import AddMedFragments.MedReviewRemindersFragment;


public class MedicationsFragment extends Fragment implements CustomAdapter.ItemClickListener {
    private Button addMedButton;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private View rootView;
    private ProgressBar progressBar;
    private TextView noMed;

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.GONE);
        noMed.setVisibility(View.GONE);
        getUserMeds();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noMed = view.findViewById(R.id.no_med_text);
        progressBar = view.findViewById(R.id.progress_circular);
        addMedButton = view.findViewById(R.id.add_med_button);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserMeds();
        recyclerView = rootView.findViewById(R.id.recyclerView);

        addMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingMedication();
            }
        });

    }

    private void getUserMeds() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(currentUser.getUid()).collection("userMeds")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> userMedIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String docName = document.getId();
                            userMedIds.add(docName);
                        }
                        if (userMedIds.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            noMed.setVisibility(View.VISIBLE);
                            return;
                        }
                        setRecyclerView(userMedIds);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setRecyclerView(List<String> userMedIds) {
        List<String> medNames = new ArrayList<>();
        List<String> medTimes = new ArrayList<>();
        List<String> medDates = new ArrayList<>();
        List<String> medForms = new ArrayList<>();
        List<String> medIds = new ArrayList<>();
        if (userMedIds.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        for (String id : userMedIds) {
            CollectionReference medsCollection = db.collection("meds");

            medsCollection.document(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String medName = documentSnapshot.getString("medName");
                                String medNextTime = documentSnapshot.getString("nextTime");
                                String medNextDate = documentSnapshot.getString("nextDate");
                                String medForm = documentSnapshot.getString("medForm");

                                medNames.add(medName);
                                medTimes.add(medNextTime);
                                medDates.add(medNextDate);
                                medForms.add(medForm);
                                medIds.add(id);
                                if (id.equals(userMedIds.get(userMedIds.size() - 1))) {
                                    noMed.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    MedRecyclerViewAdapter medRecyclerViewAdapter = new MedRecyclerViewAdapter(getActivity(), medNames, medTimes, medDates, medForms, medIds);
                                    recyclerView.setAdapter(medRecyclerViewAdapter);
                                    progressBar.setVisibility(View.GONE);
                                }


                            } else {
                                System.out.println("med does not exists");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("error to read from db");
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });


        }
    }

    private void startAddingMedication() {
        Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_medications, container, false);
        return rootView;
    }

    @Override
    public void onTimeButtonClick(View view, int position) {

    }

    @Override
    public void onDoseCountButtonClick(View view, int position) {

    }
}