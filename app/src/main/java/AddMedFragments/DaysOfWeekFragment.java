package AddMedFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaysOfWeekFragment extends Fragment {
    private ImageView back;
    private Button monday, tuesday, wednesday, thursday, friday, saturday, sunday, next;
    private Button[] buttons;
    private boolean[] clickable;
    private String documentId;
    private String[] daysOfWeek;
    private FirebaseFirestore db;
    private AddMedicationActivity addMedicationActivity;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        back = view.findViewById(R.id.back);
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);
        next = view.findViewById(R.id.next);
        addMedicationActivity = (AddMedicationActivity) requireActivity();

        documentId = getArguments().getString("documentId");
        clickable = new boolean[]{false, false, false, false, false, false, false};
        buttons = new Button[]{monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        daysOfWeek = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (int i = 0; i < buttons.length; i++) {
            int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    if (!clickable[j]) {
                        buttons[j].setBackground(getResources().getDrawable(R.drawable.list_selected_button_bg));
                        clickable[j] = true;
                    } else {
                        buttons[j].setBackground(getResources().getDrawable(R.drawable.list_button_bg));
                        clickable[j] = false;
                    }
                }
            });
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> days = new ArrayList<>();
                for (int i = 0; i < daysOfWeek.length; i++) {
                    if (clickable[i]) {
                        days.add(daysOfWeek[i]);
                    }
                }
                if (days.isEmpty()) {
                    Toast.makeText(getActivity(), "Please choose the days of the week", Toast.LENGTH_SHORT).show();
                } else {
                    addDaysOfWeekToDB(days);
                    addMedicationActivity.showMedTimeFragment();


                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideDaysOfWeekFragment();
            }
        });
    }

    private void addDaysOfWeekToDB(List<String> daysOfWeek) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("daysOfWeek", daysOfWeek);

        medsCollection
                .document(documentId)
                .update(medData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Массив успешно добавлен в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении массива в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_days_of_week, container, false);
    }
}