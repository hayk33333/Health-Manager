package AddMedFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.util.HashMap;
import java.util.Map;


public class MedEveryXWeeks extends Fragment {
    private ImageView back;
    private Button next;
    private String documentId;
    private FirebaseFirestore db;
    private NumberPicker numberPicker;
    private AddMedicationActivity addMedicationActivity;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        back = view.findViewById(R.id.back);
        numberPicker = view.findViewById(R.id.numberPicker);
        db = FirebaseFirestore.getInstance();
        documentId = getArguments().getString("documentId");
        next = view.findViewById(R.id.next);
        addMedicationActivity = (AddMedicationActivity) requireActivity();



        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(21);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedEveryXWeeksFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEveryXWeeksToDB(String.valueOf(numberPicker.getValue()));
                addMedicationActivity.showMedChooseDayFragment();

            }
        });
    }
    private void addEveryXWeeksToDB(String everyXWeeks) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("everyXWeeks", everyXWeeks);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + everyXWeeks + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + everyXWeeks + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_med_every_x_weeks, container, false);
    }
}