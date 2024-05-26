package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment implements MedTodayRecyclerView.ItemClickListener, VisitSoonRecyclerView.ItemClickListener {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView, recyclerViewVisit;
    private View rootView;
    private ProgressBar progressBar;
    private TextView noText;
    private boolean isTodayMedsExist = false;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerViewVisit = rootView.findViewById(R.id.recyclerViewVisits);
        progressBar = view.findViewById(R.id.progress_circular);
        noText = view.findViewById(R.id.no_text);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewVisit.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        getUserMeds();
    }

    private void getUserVisits() {
        db.collection("users").document(currentUser.getUid()).collection("userVisits")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> userVisits = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String docName = document.getId();
                            userVisits.add(docName);
                        }
                        if (!userVisits.isEmpty()) {

                            getSoonVisits(userVisits);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            if (!isTodayMedsExist) {

                                noText.setVisibility(View.VISIBLE);
                            }
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void getSoonVisits(List<String> userVisits) {
        List<String> visitNames = new ArrayList<>();
        List<String> visitDates = new ArrayList<>();
        List<String> visitIds = new ArrayList<>();


        for (String id : userVisits) {
            CollectionReference medsCollection = db.collection("visits");

            medsCollection.document(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                long day = documentSnapshot.getLong("day");
                                long month = documentSnapshot.getLong("month");
                                long year = documentSnapshot.getLong("year");

                                Calendar calendar = Calendar.getInstance();
                                int yearNow = calendar.get(Calendar.YEAR);
                                int monthNow = calendar.get(Calendar.MONTH) + 1;
                                int dayNow = calendar.get(Calendar.DAY_OF_MONTH);

                                Calendar targetDate = Calendar.getInstance();
                                targetDate.set((int) year, (int) month - 1, (int) day);

                                long differenceMillis = targetDate.getTimeInMillis() - calendar.getTimeInMillis();

                                int daysRemaining = (int) (differenceMillis / (1000 * 60 * 60 * 24));

                                if (daysRemaining <= 7) {
                                    visitIds.add(id);
                                    visitDates.add(String.valueOf(daysRemaining));
                                    visitNames.add(documentSnapshot.getString("visitName"));
                                    setRecyclerViewVisits(visitNames, visitDates, visitIds);
                                }


                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("error to read from db");
                        }
                    });
        }
    }

    private void setRecyclerViewVisits(List<String> visitNames, List<String> visitDates, List<String> visitIds) {
        progressBar.setVisibility(View.GONE);
        if (visitNames.isEmpty()) {
            if (!isTodayMedsExist) {

                noText.setVisibility(View.VISIBLE);
            }

        } else if (getContext() != null) {
            noText.setVisibility(View.GONE);
            recyclerViewVisit.setVisibility(View.VISIBLE);
            VisitSoonRecyclerView visitSoonRecyclerView = new VisitSoonRecyclerView(getContext(), visitNames, visitDates, visitIds);
            visitSoonRecyclerView.setClickListener((VisitSoonRecyclerView.ItemClickListener) HomeFragment.this);
            recyclerViewVisit.setAdapter(visitSoonRecyclerView);
        }
    }

    private void getUserMeds() {
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
                        if (!userMedIds.isEmpty()) {

                            getTodayMeds(userMedIds);
                        } else {
                            isTodayMedsExist = false;
                            getUserVisits();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void getTodayMeds(List<String> userMedIds) {
        List<String> todayMedIds = new ArrayList<>();
        List<String> medNames = new ArrayList<>();
        List<String> doseTypes = new ArrayList<>();
        List<String> doseCounts = new ArrayList<>();
        List<Long> differenceInMillisList = new ArrayList<>();

        for (String id : userMedIds) {
            CollectionReference medsCollection = db.collection("meds");

            medsCollection.document(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String nextDate = documentSnapshot.getString("nextDate");
                                String nextTime = documentSnapshot.getString("nextTime");
                                String doseCount = documentSnapshot.getString("doseCount");
                                String doseType = documentSnapshot.getString("doseType");
                                String medFrequency = documentSnapshot.getString("medFrequency");
                                boolean isTake = documentSnapshot.getBoolean("isTake");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                try {
                                    date = sdf.parse(nextDate);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                String[] parts = nextTime.split(":");
                                int hour = Integer.parseInt(parts[0]);
                                int minute = Integer.parseInt(parts[1]);

                                Calendar calendarToday = Calendar.getInstance();
                                Calendar calendarDate = Calendar.getInstance();
                                calendarDate.setTime(date);
                                calendarDate.set(Calendar.HOUR_OF_DAY, hour);
                                calendarDate.set(Calendar.MINUTE, minute);
                                calendarDate.set(Calendar.SECOND, 0);
                                calendarDate.set(Calendar.MILLISECOND, 0);

                                long currentTimeInMillis = calendarToday.getTimeInMillis();
                                long nextTimeInMillis = calendarDate.getTimeInMillis();
                                if (medFrequency.equals("twiceDay")) {
                                    String firstDoseCount = documentSnapshot.getString("firstDoseCount");
                                    String secondDoseCount = documentSnapshot.getString("secondDoseCount");
                                    String medFirstTime = documentSnapshot.getString("medFirstTime");


                                    if (nextTime.equals(medFirstTime)) {
                                        doseCount = firstDoseCount;
                                    } else {
                                        doseCount = secondDoseCount;
                                    }

                                } else if (medFrequency.equals("moreTimes")) {
                                    List<String> medTimes = (List<String>) documentSnapshot.get("times");
                                    List<String> medDoses = (List<String>) documentSnapshot.get("doses");

                                    for (String t : medTimes) {
                                        if (t.equals(nextTime)) {
                                            doseCount = medDoses.get(medTimes.indexOf(t));
                                        }
                                    }


                                }


                                if (isSameDay(calendarToday, calendarDate)) {
                                    long differenceInMillis = nextTimeInMillis - currentTimeInMillis;
                                    differenceInMillisList.add(differenceInMillis);
                                    todayMedIds.add(id);
                                    doseTypes.add(doseType);
                                    doseCounts.add(doseCount);
                                    medNames.add(documentSnapshot.getString("medName"));
                                }

                                if (id.equals(userMedIds.get(userMedIds.size() - 1))) {
                                    setRecyclerView(todayMedIds, medNames, differenceInMillisList, doseCounts, doseTypes);
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
                        }
                    });
        }
    }

    private void setRecyclerView(List<String> todayMedIds, List<String> medNames, List<Long> differenceInMillisList, List<String> doseCounts, List<String> doseTypes) {
        progressBar.setVisibility(View.GONE);
        if (todayMedIds.isEmpty()) {
            isTodayMedsExist = false;

        } else if (getContext() != null) {
            isTodayMedsExist = true;
            noText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            MedTodayRecyclerView medTodayRecyclerView = new MedTodayRecyclerView(getContext(), todayMedIds, medNames, differenceInMillisList, doseCounts, doseTypes);
            medTodayRecyclerView.setClickListener((MedTodayRecyclerView.ItemClickListener) HomeFragment.this);
            recyclerView.setAdapter(medTodayRecyclerView);
        }
        getUserVisits();

    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }


    @Override
    public void onTakeClick(View view, int position) {
        getUserMeds();
    }


    @Override
    public void onDoneClick(View view, int position) {
        getUserVisits();
    }
}
