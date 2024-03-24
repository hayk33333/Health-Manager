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


public class EveryXHoursFragment extends Fragment {
    private ImageView back;
    private NumberPicker numberPicker;
    private Button next;
    private String documentId;
    private FirebaseFirestore db;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        numberPicker = view.findViewById(R.id.numberPicker);
        db = FirebaseFirestore.getInstance();
        next = view.findViewById(R.id.next);
        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(6);
        documentId = getArguments().getString("documentId");

        String[] displayedValues = {"1/2", "1", "2", "3", "4", "6", "8"};
        numberPicker.setDisplayedValues(displayedValues);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEveryXHoursToDB(String.valueOf(displayedValues[numberPicker.getValue()]));
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideEveryXHoursFragment();
            }
        });
    }

    private void addEveryXHoursToDB(String everyXHours) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("everyXHours", everyXHours);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + everyXHours + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + everyXHours + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_every_x_hours, container, false);
    }
}