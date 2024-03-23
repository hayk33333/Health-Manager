package AddMedFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MedReviewRemindersFragment extends Fragment implements CustomAdapter.ItemClickListener {
    private ImageView back;
    private String documentId;
    private int howTimesInDay = 0;
    private View rootView;
    private String firstTime;
    private AddMedicationActivity addMedicationActivity;
    private ProgressBar progressBar;
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> doses = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayout timePicker, dosePicker;
    private TextView ok, okDose;
    private NumberPicker hour, minute;
    private FirebaseFirestore db;
    private Button next;
    private boolean muchTimes;
    private String doseType;
    private String doseCount;
    private EditText doseEt;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doseEt = view.findViewById(R.id.med_dose);
        okDose = view.findViewById(R.id.ok_dose);
        dosePicker = view.findViewById(R.id.dose_picker);
        documentId = getArguments().getString("documentId");
        recyclerView = rootView.findViewById(R.id.recycler_view);
        back = view.findViewById(R.id.back);
        db = FirebaseFirestore.getInstance();
        ok = view.findViewById(R.id.ok);
        timePicker = view.findViewById(R.id.time_picker);
        hour = view.findViewById(R.id.hour_time_picker);
        minute = view.findViewById(R.id.minute_time_picker);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        progressBar = view.findViewById(R.id.progress_circular);
        next = view.findViewById(R.id.next);
        progressBar.setVisibility(View.VISIBLE);
        getDataFromDb();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedReviewRemindersFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muchTimes = false;
                addTimesAndDosesToDB();
                addMedicationActivity.showMedAdditionalInformationFragment();
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
                        System.out.println("error to read from db");
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


        }
        for (int i = 0; i < howTimesInDay; i++) {
            doses.add(doseCount);
        }

        System.out.println(doses);
        System.out.println(howTimesInDay);
        CustomAdapter adapter = new CustomAdapter(getActivity(), times, doseType, doses);
        adapter.setClickListener(MedReviewRemindersFragment.this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void addTimesAndDosesToDB(){
        CollectionReference medsCollection = db.collection("meds");
        sortTimesAndDoses(times, doses);
        if (muchTimes){
            Toast.makeText(addMedicationActivity, "Times can not be much", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> medData = new HashMap<>();
        medData.put("times", times);
        medData.put("doses", doses);

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


    private void getDataFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            firstTime = documentSnapshot.getString("medFirstTime");
                            doseType = documentSnapshot.getString("doseType");
                            doseCount = documentSnapshot.getString("firstDoseCount");
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
                CustomAdapter adapter = new CustomAdapter(getActivity(), times, doseType,doses);
                adapter.setClickListener(MedReviewRemindersFragment.this);
                recyclerView.setAdapter(adapter);
                timePicker.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
                recyclerView.setEnabled(true);

            }
        });

    }
    private void sortTimesAndDoses(ArrayList<String> times, ArrayList<String> medDose) {
        ArrayList<String> initialTimes = new ArrayList<>();
        initialTimes.addAll(times);
        ArrayList<String> initialDoses = new ArrayList<>();
        initialDoses.addAll(medDose);

        times.sort(new Comparator<String>() {
            @Override
            public int compare(String time1, String time2) {
                String[] parts1 = time1.split(":");
                String[] parts2 = time2.split(":");

                int hour1 = Integer.parseInt(parts1[0]);
                int minute1 = Integer.parseInt(parts1[1]);

                int hour2 = Integer.parseInt(parts2[0]);
                int minute2 = Integer.parseInt(parts2[1]);
                if (hour1 == 0){
                    hour1 = 24;
                }
                if (hour2 == 0){
                    hour2 = 24;
                }
                if (hour1 == hour2 && minute1 == minute2){
                    muchTimes = true;
                }

                if (hour1 != hour2) {
                    return Integer.compare(hour1, hour2);
                } else {
                    return Integer.compare(minute1, minute2);
                }
            }
        });
        for (String time:initialTimes){
            doses.set(times.indexOf(time), initialDoses.get(initialTimes.indexOf(time)));
        }



    }





    @Override
    public void onDoseCountButtonClick(View view, int position) {
        dosePicker.setVisibility(View.VISIBLE);
        okDose.setVisibility(View.VISIBLE);
        okDose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dose = doseEt.getText().toString();

                if (!dose.isEmpty()){
                    doses.set(position, dose);
                }
                dosePicker.setVisibility(View.GONE);
                okDose.setVisibility(View.GONE);
                CustomAdapter adapter = new CustomAdapter(getActivity(), times, doseType, doses);
                adapter.setClickListener(MedReviewRemindersFragment.this);
                recyclerView.setAdapter(adapter);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

    }
}
