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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;
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
        timeUnits = new String[]{getString(R.string.minute_s), getString(R.string.hour_s),
                getString(R.string.day_s), getString(R.string.week_s), getString(R.string.month_s), getString(R.string.year_s)};
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

                String dataType1 = timeUnits[unitTime.getValue()];
                addNotificationDataTODB(dataCount, dataType1);
            }
        });
    }

    private void isDataCorrect() {
        CollectionReference medsCollection = db.collection("visits");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String visitTime = documentSnapshot.getString("visitTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            String dataType = documentSnapshot.getString("notificationDataType");
                            String dataCount = documentSnapshot.getString("notificationDataCount");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            String[] parts = visitTime.split(":");
                            int hour;
                            int minute;

                            String hourStr = parts[0];
                            hour = Integer.parseInt(hourStr);

                            String minuteStr = parts[1];
                            minute = Integer.parseInt(minuteStr);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(year));
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            System.out.println(calendar.getTimeInMillis());
                            switch (dataType) {
                                case "minute(s)":
                                    calendar.add(Calendar.MINUTE, -Integer.valueOf(dataCount));
                                    break;
                                case "hour(s)":
                                    calendar.add(Calendar.HOUR, -Integer.valueOf(dataCount));
                                    break;
                                case "day(s)":
                                    calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(dataCount));
                                    break;
                                case "week(s)":
                                    calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(dataCount) * 7);
                                    break;
                                case "month(s)":
                                    calendar.add(Calendar.MONTH, -Integer.valueOf(dataCount));
                                    break;
                                case "year(s)":
                                    calendar.add(Calendar.YEAR, -Integer.valueOf(dataCount));
                                    break;
                            }
                            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                                addVisitActivity.showVisitAdditionalInfoFragment();
                            } else {
                                Toast.makeText(addVisitActivity, R.string.you_will_never_receive_a_notification_because_the_time_you_specified_has_already_passed, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            System.out.println("med does not exists");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("error to read from db");
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
                        isDataCorrect();
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