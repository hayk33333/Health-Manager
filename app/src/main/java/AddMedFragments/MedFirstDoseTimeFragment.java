package AddMedFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;


public class MedFirstDoseTimeFragment extends Fragment {

    private ImageView back;
    private Button next;
    private String documentId;
    private FirebaseFirestore db;
    private EditText medDose;
    private NumberPicker doseTypes;
    private AddMedicationActivity addMedicationActivity;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String[] types;
    private String medFrequencyInDay, medForm;
    private Drawable red_et_background, et_background;
    private NumberPicker hour, minute;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_circular);
        linearLayout = view.findViewById(R.id.linear_layout);
        medDose = view.findViewById(R.id.med_dose);
        doseTypes = view.findViewById(R.id.dose_types);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        documentId = getArguments().getString("documentId");
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        db = FirebaseFirestore.getInstance();
        getMedFrequencyInDayFromDb();
        getMedForm();
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
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
                String type = null;
                if (!medForm.equals("other")) {
                    type = types[doseTypes.getValue()];
                }

                if (dose.isEmpty()) {
                    medDose.setBackground(red_et_background);
                    return;
                }
                addMedDoseTypeAndCountToDB(type, dose);
                addMedFirstTimeToDB(String.valueOf(hourValue) + ":" + String.valueOf(minuteValue * 5));
                try {
                    if (medFrequencyInDay.equals("twiceDay")) {
                        addMedicationActivity.showMedSecondDoseTimeFragment();
                    } else if (medFrequencyInDay.equals("moreTimes")) {
                        addMedicationActivity.showMedReviewRemindersFragment();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedFirstDoseFragment();
            }
        });


    }

    private void addMedFirstTimeToDB(String medFirstTime) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medFirstTime", medFirstTime);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medFirstTime + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medFirstTime + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    private void getMedFrequencyInDayFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");
        final String[] frequency = new String[1];
        frequency[0] = "";

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Получаем данные из документа
                            frequency[0] = documentSnapshot.getString("medFrequencyInDay");
                            medFrequencyInDay = frequency[0];
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

    private void getMedForm() {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            medForm = documentSnapshot.getString("medForm");
                            setMedDoseTypes(medForm);
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

    private void setMedDoseTypes(String medForm) {
        int typesCount = 0;
        switch (medForm) {
            case "pill":
                types = new String[]{getString(R.string.pill_s)};
                doseTypes.setMinValue(0);
                doseTypes.setMaxValue(typesCount);
                doseTypes.setDisplayedValues(types);
                break;
            case "injection":
                types = new String[]{getString(R.string.ml), getString(R.string.syringe_s), getString(R.string.unit), getString(R.string.ampules_s), getString(R.string.vial_s)};
                typesCount = 4;
                doseTypes.setMinValue(0);
                doseTypes.setMaxValue(typesCount);
                doseTypes.setDisplayedValues(types);
                break;
            case "solution":
                types = new String[]{getString(R.string.ml), getString(R.string.cup_s), getString(R.string.drop_s), getString(R.string.ampules_s), getString(R.string.unit)};
                typesCount = 4;
                doseTypes.setMinValue(0);
                doseTypes.setMaxValue(typesCount);
                doseTypes.setDisplayedValues(types);
                break;
            case "drops":
                types = new String[]{getString(R.string.ml), getString(R.string.drop_s), getString(R.string.unit)};
                typesCount = 2;
                doseTypes.setMinValue(0);
                doseTypes.setMaxValue(typesCount);
                doseTypes.setDisplayedValues(types);
                break;
            case "powder":
                types = new String[]{getString(R.string.packet_s), getString(R.string.gram_s), getString(R.string.tablespoon_s), getString(R.string.teaspoon_s), getString(R.string.unit)};
                typesCount = 4;
                doseTypes.setMinValue(0);
                doseTypes.setMaxValue(typesCount);
                doseTypes.setDisplayedValues(types);
                break;
            default:
                doseTypes.setVisibility(View.GONE);
                types = new String[0];
                break;
        }
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void addMedDoseTypeAndCountToDB(String type, String dose) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("doseType", type);
        medData.put("firstDoseCount", dose);


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_first_dose_time, container, false);
    }
}