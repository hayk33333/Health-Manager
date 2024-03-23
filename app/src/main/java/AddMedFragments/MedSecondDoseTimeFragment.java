package AddMedFragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class MedSecondDoseTimeFragment extends Fragment {
    private ImageView back;
    private Button next;
    private String documentId;
    private AddMedicationActivity addMedicationActivity;
    private String firstTime = "";
    private NumberPicker hour, minute;
    private FirebaseFirestore db;
    private EditText medDose;
    private Drawable red_et_background, et_background;
    private String[] times;
    private String[] displayedValues1 = new String[12];

    private boolean muchTimes;



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        medDose = view.findViewById(R.id.med_dose);
        documentId = getArguments().getString("documentId");
        getFirstTimeFromDB();
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
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
                medDose.setBackground(et_background);
                String dose = medDose.getText().toString();

                if (dose.isEmpty()) {
                    medDose.setBackground(red_et_background);
                    return;
                }
                String secondTime = String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5);
                times = new String[]{firstTime,secondTime};
                muchTimes = false;

                sortTimes();
                if (muchTimes){
                    Toast.makeText(getActivity(), "Times can not be much", Toast.LENGTH_SHORT).show();
                    return;
                }
                addMedTimesToDB(times[0], times[1]);
                addMedDoseCountToDB(dose);
                addMedicationActivity.showMedAdditionalInformationFragment();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedSecondDoseFragment();
            }
        });


    }

    private void addMedTimesToDB(String medFirstTime,String medSecondTime) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medFirstTime", medFirstTime);
        medData.put("medSecondTime", medSecondTime);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medSecondTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medSecondTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    private void addMedDoseCountToDB(String dose) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("secondDoseCount", dose);


        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение ' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }
    private void sortTimes() {
        Arrays.sort(times, new Comparator<String>() {
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
    }
    private void getFirstTimeFromDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Получаем данные из документа
                            firstTime = documentSnapshot.getString("medFirstTime");

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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_second_dose_time, container, false);
    }
}