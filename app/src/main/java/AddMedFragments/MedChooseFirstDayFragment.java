package AddMedFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class MedChooseFirstDayFragment extends Fragment {
   private NumberPicker day, month, year;
   private ImageView back;
   private Button next;
   private FirebaseFirestore db;
   private AddMedicationActivity addMedicationActivity;
   private String documentId;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        day = view.findViewById(R.id.day);
        next = view.findViewById(R.id.next);
        documentId = getArguments().getString("documentId");
        month = view.findViewById(R.id.month);
        year = view.findViewById(R.id.year);
        String[] months = {getString(R.string.jan), getString(R.string.feb), getString(R.string.mar),
                getString(R.string.apr), getString(R.string.may), getString(R.string.jun), getString(R.string.jul),
                getString(R.string.aug), getString(R.string.sept), getString(R.string.oct), getString(R.string.nov), getString(R.string.dec)};
        year.setMinValue(getYear());
        year.setMaxValue(2100);
        year.setWrapSelectorWheel(false);
        back = view.findViewById(R.id.back);
        year.setValue(getYear());
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setValue(getMonthValue());
        month.setDisplayedValues(months);
        day.setMinValue(1);
        setDay();
        day.setValue(getDay());
        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDay();
            }
        });
        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDay();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYearMonthDDayToDb(year.getValue(), month.getValue(), day.getValue());
                addMedicationActivity.showMedTimeFragment();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedEveryOtherDayFragment();
            }
        });

    }

    private void setDay() {
        int selectedMonth = month.getValue();
        int selectedYear = year.getValue();
        int maxDay;

        switch (selectedMonth) {
            case 2:
                maxDay = YearMonth.of(selectedYear, Month.FEBRUARY).lengthOfMonth();
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                maxDay = 30;
                break;
            default:
                maxDay = 31;
                break;
        }

        day.setMaxValue(maxDay);
    }
    private void addYearMonthDDayToDb(int selectedYear, int selectedMonth, int selectedDay) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("year", selectedYear);
        medData.put("month", selectedMonth);
        medData.put("day", selectedDay);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }



    private int getYear() {
        return LocalDate.now().getYear();
    }

    private int getMonthValue() {
        return LocalDate.now().getMonthValue();
    }
    private int getDay() {
        return LocalDate.now().getDayOfMonth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_choose_first_day, container, false);
    }
}