package AddMedFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class AddMedCountFragment extends Fragment {
    AddMedicationActivity addMedicationActivity;
    ImageView back;
    EditText pillsCount, pillsRemindCount;
    FirebaseFirestore db;
    String documentId;
    Button next;
    Drawable red_et_background,et_background;




    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        back = view.findViewById(R.id.back);
        db = FirebaseFirestore.getInstance();
        next = view.findViewById(R.id.next);
        pillsCount = view.findViewById(R.id.pills_count);
        pillsRemindCount = view.findViewById(R.id.pills_remind_count);
        documentId = getArguments().getString("documentId");
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        pillsRemindCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pillsRemindCount.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pillsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pillsRemindCount.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pillsCount.setBackground(et_background);
                pillsRemindCount.setBackground(et_background);
                String countStr = pillsCount.getText().toString();
                String remindCountStr = pillsRemindCount.getText().toString();
                if (countStr.isEmpty()){
                    pillsCount.setBackground(red_et_background);
                }
                if (remindCountStr.isEmpty()){
                    pillsRemindCount.setBackground(red_et_background);
                }
                if (!countStr.isEmpty() && !remindCountStr.isEmpty()){
                    int count = Integer.parseInt(pillsCount.getText().toString());
                    int remindCount = Integer.parseInt(pillsRemindCount.getText().toString());
                    addMedCountToDB(count, remindCount);
                    addMedicationActivity.hideAddMedCountFragment();

                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideAddMedCountFragment();
            }
        });
    }
    private void addMedCountToDB(int medCount, int medRemindCount) {
        CollectionReference medsCollection = db.collection("meds");

        Map<String, Object> medData = new HashMap<>();
        medData.put("medCount", medCount);
        medData.put("medRemindCount", medRemindCount);

        medsCollection
                .document(documentId)
                .update(medData)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_med_count, container, false);
    }
}