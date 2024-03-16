package com.hayk.healthmanagerregistration;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedReviewRemindersFragment extends Fragment implements CustomAdapter.ItemClickListener {
    ImageView back;
    String documentId;
    int howTimesInDay = 0;
    View rootView;
    String firstTime;
    AddMedicationActivity addMedicationActivity;
    ProgressBar progressBar;
    ArrayList<String> times = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayout timePicker;
    TextView ok;
    NumberPicker hour, minute;
    FirebaseFirestore db;
    Button next;



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        back = view.findViewById(R.id.back);
        db = FirebaseFirestore.getInstance();
        ok = view.findViewById(R.id.ok);
        timePicker = view.findViewById(R.id.time_picker);
        hour = view.findViewById(R.id.hour_time_picker);
        minute = view.findViewById(R.id.minute_time_picker);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        progressBar = view.findViewById(R.id.progress_circular);
        documentId = getArguments().getString("documentId");
        next = view.findViewById(R.id.next);
        progressBar.setVisibility(View.VISIBLE);
        getFirstTimeFromDb();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedReviewRemindersFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimesToDB();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_med_review_reminders, container, false);
        return rootView;
    }

    private void getHowTimesInDayFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String howTimes = documentSnapshot.getString("howTimesDay");
                            howTimesInDay = Integer.parseInt(howTimes);
                            setRecycleView(howTimesInDay);
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

    private void setRecycleView(int howTimesInDay) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String[] parts = firstTime.split(":");
        int firstHour;
        int firstMinute;


        String hourStr = parts[0];
        firstHour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        firstMinute = Integer.parseInt(minuteStr);

        int currentMinute = (1380 - (firstMinute + firstHour * 60)) / (howTimesInDay - 1);
        int firstTimeMinute = firstHour * 60 + firstMinute;
        Toast.makeText(getActivity(), String.valueOf(howTimesInDay), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < howTimesInDay; i++) {
            int hour, minute;
            hour = firstTimeMinute / 60;
            minute = firstTimeMinute - hour * 60;
            int a = minute % 10;
            if (a != 5 && a != 0) {
                if (a > 5) {
                    minute += 10 - a;
                } else {
                    minute -= a;
                }
            }
            String minuteS = String.valueOf(minute);
            String hourS = String.valueOf(hour);
            if (minuteS.equals("0")) {
                minuteS = "00";
            } else if (minuteS.equals("60")) {
                hour++;
                hourS = String.valueOf(hour);
                minuteS = "00";
            }
            firstTimeMinute += currentMinute;
            String result = hourS + ":" + minuteS;
            times.add(result);
            System.out.println(result);
        }
        CustomAdapter adapter = new CustomAdapter(getActivity(), times);
        adapter.setClickListener(MedReviewRemindersFragment.this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
    private void addTimesToDB(){
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("times", times);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Массив успешно добавлен в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении массива в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    private void getFirstTimeFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            firstTime = documentSnapshot.getString("medFirstTime");
                            getHowTimesInDayFromDb();


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


    @Override
    public void onTimeButtonClick(View view, int position) {
        timePicker.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        hour.setMinValue(0);
        hour.setMaxValue(23);
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
        recyclerView.setEnabled(false);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourValue = hour.getValue();
                int minuteValue = minute.getValue() * 5;
                String hourString = String.valueOf(hourValue);
                String minuteString = (minuteValue < 10) ? "0" + String.valueOf(minuteValue) : String.valueOf(minuteValue);
                String time = hourString + ":" + minuteString;
                times.set(position, time);
                CustomAdapter adapter = new CustomAdapter(getActivity(), times);
                adapter.setClickListener(MedReviewRemindersFragment.this);
                recyclerView.setAdapter(adapter);
                timePicker.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
                recyclerView.setEnabled(true);

            }
        });

    }




    @Override
    public void onPillsButtonClick(View view, int position) {
        Toast.makeText(getActivity(), "pill" + String.valueOf(position), Toast.LENGTH_SHORT).show();

    }
}
