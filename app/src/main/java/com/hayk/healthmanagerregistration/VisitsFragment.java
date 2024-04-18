package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class VisitsFragment extends Fragment {
    private Button addVisit;
    private RecyclerView recyclerView;
    private View rootView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private TextView noVisit;





    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        addVisit =view.findViewById(R.id.add_visit_button);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        noVisit = view.findViewById(R.id.no_visit_text);
        progressBar = view.findViewById(androidx.transition.R.id.progress_circular);
        getUserVisits();

        addVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddVisitActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getUserVisits() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(currentUser.getUid()).collection("userVisits")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> userVisitIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String docName = document.getId();
                            userVisitIds.add(docName);
                        }
                        if (userVisitIds.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            noVisit.setVisibility(View.VISIBLE);
                            return;
                        }
                        setRecyclerView(userVisitIds);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setRecyclerView(List<String> userVisitIds) {
        List<String> visitNames = new ArrayList<>();
        List<String> visitDates = new ArrayList<>();
        List<String> hospitalNames = new ArrayList<>();
        List<String> doctorNames = new ArrayList<>();


        for (String id : userVisitIds) {
            CollectionReference medsCollection = db.collection("visits");

            medsCollection.document(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String visitName = documentSnapshot.getString("visitName");
                                String visitDay = String.valueOf(documentSnapshot.getLong("day"));
                                String visitMonth = String.valueOf(documentSnapshot.getLong("month"));
                                String visitYear = String.valueOf(documentSnapshot.getLong("year"));
                                String visitTime = documentSnapshot.getString("visitTime");
                                String doctorName = documentSnapshot.getString("doctorName");
                                String hospitalName = documentSnapshot.getString("hospitalName");
                                if (doctorName == null){
                                    doctorName = "";
                                }if (hospitalName == null){
                                    hospitalName = "";
                                }
                                if (Integer.parseInt(visitDay) < 10) {
                                    visitDay = String.format("%02d", Integer.parseInt(visitDay));
                                }

                                if (Integer.parseInt(visitMonth) < 10) {
                                    visitMonth = String.format("%02d", Integer.parseInt(visitMonth));
                                }

                                String[] timeParts = visitTime.split(":");
                                int hours = Integer.parseInt(timeParts[0]);
                                int minutes = Integer.parseInt(timeParts[1]);

                                String formattedHours = (hours < 10) ? String.format("%02d", hours) : String.valueOf(hours);

                                String formattedMinutes = (minutes < 10) ? String.format("%02d", minutes) : String.valueOf(minutes);

                                visitTime = formattedHours + ":" + formattedMinutes;
                                String date = visitDay + "/" + visitMonth + "/" + visitYear + " " + visitTime;
                                visitDates.add(date);
                                visitNames.add(visitName);
                                hospitalNames.add(hospitalName);
                                doctorNames.add(doctorName);

                                if (id.equals(userVisitIds.get(userVisitIds.size() - 1))) {
                                    noVisit.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    VisitRecyclerViewAdapter visitRecyclerViewAdapter = new VisitRecyclerViewAdapter(getActivity(), visitNames, visitDates, doctorNames, hospitalNames);
                                    recyclerView.setAdapter(visitRecyclerViewAdapter);
                                    progressBar.setVisibility(View.GONE);
                                }


                            } else {
                                System.out.println("med does not exists");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            System.out.println("error to read from db");
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_visits, container, false);
        return rootView;
    }
}