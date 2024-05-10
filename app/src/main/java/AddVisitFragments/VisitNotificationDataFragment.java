package AddVisitFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitNotificationDataFragment extends Fragment {
    private Button next;
    private EditText amountTime;
    private NumberPicker unitTime;
    private String[] timeUnits;
    private String documentId;
    private FirebaseFirestore db;
    private AddVisitActivity addVisitActivity;
    private ImageView back;
    private Drawable red_et_background, et_background;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        next = view.findViewById(R.id.next);
        amountTime = view.findViewById(R.id.amount_of_time);
        back = view.findViewById(R.id.back);
        unitTime = view.findViewById(R.id.unit_of_time);
        addVisitActivity = (AddVisitActivity) requireActivity();
        db = FirebaseFirestore.getInstance();
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        documentId = getArguments().getString("documentId");
        timeUnits = new String[]{"minute(s)", "hour(s)", "day(s)", "week(s)", "month(s)", "year(s)"};
        unitTime.setMinValue(0);
        unitTime.setMaxValue(5);
        unitTime.setDisplayedValues(timeUnits);
        unitTime.setValue(2);
        amountTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amountTime.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addVisitActivity.hideVisitNotificationDataFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataCount = amountTime.getText().toString().trim();
                if (dataCount.isEmpty() || Integer.parseInt(dataCount) == 0) {
                    amountTime.setBackground(red_et_background);
                    return;
                }
                String dataType = timeUnits[unitTime.getValue()];
                addNotificationDataTODB(dataCount, dataType);
                addVisitActivity.showVisitAdditionalInfoFragment();
            }
        });
    }

    private void addNotificationDataTODB(String dataCount, String dataType) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> data = new HashMap<>();
        data.put("notificationDataCount", dataCount);
        data.put("notificationDataType", dataType);

        visitsCollection
                .document(documentId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение '" + "' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '" + "' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_notification_data, container, false);
    }
}