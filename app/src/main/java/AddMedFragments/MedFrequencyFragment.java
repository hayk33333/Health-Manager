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


public class MedFrequencyFragment extends Fragment {
    private ImageView back;
    private Button everyDay, everyOtherDay, everyXDays, specificDays, everyXWeeks, everyXMonths;
    private String documentId;
    private FirebaseFirestore db;
    private Button[] buttons;
    private String[] medFrequency;
    private String[] functionNames;
    private AddMedicationActivity addMedicationActivity;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        everyDay = view .findViewById(R.id.every_day);
        everyOtherDay = view.findViewById(R.id.every_other_day);
        everyXDays = view.findViewById(R.id.every_x_days);
        specificDays = view.findViewById(R.id.specific_days);
        everyXWeeks = view.findViewById(R.id.every_x_weeks);
        everyXMonths = view.findViewById(R.id.every_x_months);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        buttons = new Button[]{everyDay, everyOtherDay, everyXDays, specificDays, everyXWeeks, everyXMonths};
        medFrequency = new String[]{ "everyDay", "everyOtherDay", "everyXDays", "specificDays", "everyXWeeks", "everyXMonths"};
        functionNames = new String[]{"showMedFrequencyInDayFragment","showMedChooseDayFragment","showMedEveryXDaysFragment",
                                     "showDaysOfWeekFragment", "showMedEveryXWeeksFragment", "showMedEveryXMonthsFragment"};
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
        for (int i = 0; i < buttons.length; i++) {
            int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMedFrequencyToDB(medFrequency[j]);
                    callFunctionByName(addMedicationActivity, functionNames[j]);
                }
            });
        }

       
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedFrequencyFragment();
            }
        });
    }
    private void addMedFrequencyToDB(String medFrequency) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medFrequency", medFrequency);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + medFrequency + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + medFrequency + "' в коллекцию 'meds': " + e.getMessage());
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
        return inflater.inflate(R.layout.fragment_med_frequency, container, false);
    }
}