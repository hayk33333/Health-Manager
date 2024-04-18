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


public class MedEveryXMonths extends Fragment {
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
        numberPicker.setMaxValue(12);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedEveryXMonthsFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEveryXMonthsToDB(String.valueOf(numberPicker.getValue()));
                addMedicationActivity.showMedChooseDayFragment();
            }
        });
    }
    private void addEveryXMonthsToDB(String everyXMonths) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("everyXMonths", everyXMonths);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + everyXMonths + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + everyXMonths + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_every_x_months, container, false);
    }
}