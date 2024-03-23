package AddMedFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class MedFrequencyInDayFragment extends Fragment {
    ImageView back;
    Button onceDay, moreTimes, twiceDay, everyXHours;
    AddMedicationActivity addMedicationActivity;
    private FirebaseFirestore db;
    Button[] buttons;
    String[] medFrequencyInDay;
    String[] functionNames;
    String documentId;





    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onceDay = view.findViewById(R.id.once_day);
        moreTimes = view.findViewById(R.id.more_times);
        back = view.findViewById(R.id.back);
        twiceDay = view.findViewById(R.id.twice_day);
        //everyXHours = view.findViewById(R.id.every_x_hours);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        buttons = new Button[]{onceDay, moreTimes, twiceDay};
        medFrequencyInDay = new String[]{"onceDay", "moreTimes", "twiceDay"};
        functionNames = new String[]{"showMedTimeFragment","showHowTimesDayFragment","showMedFirstDoseTimeFragment",};
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
        for (int i = 0; i < buttons.length; i++) {
            int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMedFrequencyInDayToDB(medFrequencyInDay[j]);
                    callFunctionByName(addMedicationActivity, functionNames[j]);
                }
            });
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedFrequencyInDayFragment();
            }
        });
    }
    private void addMedFrequencyInDayToDB(String medFrequencyInDay) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medFrequencyInDay", medFrequencyInDay);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medFrequencyInDay + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medFrequencyInDay + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }
    public static void callFunctionByName(AddMedicationActivity activity, String functionName) {
        try {
            Method method = AddMedicationActivity.class.getMethod(functionName);

            method.invoke(activity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_frequency_in_day, container, false);
    }
}