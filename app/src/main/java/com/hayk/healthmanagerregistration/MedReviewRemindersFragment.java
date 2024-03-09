package com.hayk.healthmanagerregistration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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
import java.util.List;

public class MedReviewRemindersFragment extends Fragment implements CustomAdapter.ItemClickListener {
    ImageView back;
    String documentId;
    int howTimesInDay = 0;
    View rootView;
    String firstTime;
    AddMedicationActivity addMedicationActivity;
    ProgressBar progressBar;
    List<String> times = new ArrayList<>();



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back = view.findViewById(R.id.back);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        progressBar = view.findViewById(R.id.progress_circular);
        documentId = getArguments().getString("documentId");
        progressBar.setVisibility(View.VISIBLE);
        getFirstTimeFromDb();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedReviewRemindersFragment();
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
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
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
                if (a > 5){
                    minute += 10 - a;
                }else {
                    minute -= a;
                }
            }
            String minuteS = String.valueOf(minute);
            String hourS = String.valueOf(hour);
            if (minuteS.equals("0")) {
                minuteS = "00";
            }else if (minuteS.equals("60") ){
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
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        TimerPickerFragment timerPickerFragment = new TimerPickerFragment();
        timerPickerFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, timerPickerFragment)
                .commit();
    }
    public void updateTime(String newTime, int position) {
        // Обновляем время в списке на новое
        times.set(position, newTime); // Предполагается, что times - это ваш список строк, используемый для хранения времени
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), times);

        // Теперь обновляем данные в адаптере
        customAdapter.updateData(times);
    }

    @Override
    public void onPillsButtonClick(View view, int position) {
        Toast.makeText(getActivity(), "pill" + String.valueOf(position), Toast.LENGTH_SHORT).show();

    }
}
