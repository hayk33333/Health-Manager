package com.hayk.healthmanagerregistration;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedInfoActivity extends AppCompatActivity {
    private TextView medName, medForm, medReminders, medTimes, medFood, medInstruction, doseTypeTV;
    private ImageView back, delete;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private LinearLayout medAmount;
    private Button plusCount, minusCount;
    private EditText amountEt;
    private String medId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);
        doseTypeTV = findViewById(R.id.dose_type_tv);
        plusCount = findViewById(R.id.plus_count);
        minusCount = findViewById(R.id.minus_count);
        amountEt = findViewById(R.id.amount_et);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        medName = findViewById(R.id.med_name);
        medForm = findViewById(R.id.med_form);
        medReminders = findViewById(R.id.med_reminders);
        medTimes = findViewById(R.id.med_times);
        medAmount = findViewById(R.id.med_amount);
        medFood = findViewById(R.id.med_food);
        medInstruction = findViewById(R.id.med_instruction);
        medId = getIntent().getStringExtra("medId");
        getData(medId);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteVisit(medId);
            }
        });
    }

    private void getData(String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medNameStr = documentSnapshot.getString("medName");
                            String medFormStr = documentSnapshot.getString("medForm");
                            String medRemindersStr = documentSnapshot.getString("medFrequency");
                            String medInstructionStr = documentSnapshot.getString("medInstruction");
                            final String[] medCount = {String.valueOf(documentSnapshot.getLong("medCount"))};
                            final String[] doseType = {documentSnapshot.getString("doseType")};
                            String medFoodStr = documentSnapshot.getString("medWithFood");
                            medName.setText(medNameStr);
                            medForm.setText(getString(R.string.med_form) + medFormStr);
                            String reminderText = getString(R.string.reminders);
                            String medListText = getString(R.string.medication_list);

                            switch (medRemindersStr) {
                                case "onceDay":
                                    String medTime = documentSnapshot.getString("medTime");
                                    String doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    reminderText += "Once a day";
                                    break;
                                case "twiceDay":
                                    String medFirstTime = documentSnapshot.getString("medFirstTime");
                                    String doseFirstCount = documentSnapshot.getString("firstDoseCount");
                                    String medSecondTime = documentSnapshot.getString("medSecondTime");
                                    String doseSecondCount = documentSnapshot.getString("secondDoseCount");
                                    medFirstTime = formatTime(medFirstTime);
                                    medSecondTime = formatTime(medSecondTime);
                                    medListText += medFirstTime + " " + doseFirstCount + doseType[0] + ", " + medSecondTime + " " + doseSecondCount + doseType[0];
                                    reminderText += "Twice a day";
                                    break;
                                case "everyOtherDay":
                                    medTime = documentSnapshot.getString("medTime");
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    reminderText += "Every other day";
                                    break;
                                case "everyXDays":
                                    medTime = documentSnapshot.getString("medTime");
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xDays = documentSnapshot.getString("everyXDays");
                                    reminderText += "Every " + xDays + getString(R.string.Days);
                                    break;
                                case "specificDays":
                                    medTime = documentSnapshot.getString("medTime");
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    ArrayList<String> days = (ArrayList<String>) documentSnapshot.get("daysOfWeek");
                                    reminderText += "On ";
                                    for (String day : days) {
                                        reminderText += day + " ";
                                    }
                                    break;
                                case "everyXWeeks":
                                    medTime = documentSnapshot.getString("medTime");
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xWeeks = documentSnapshot.getString("everyXWeeks");
                                    reminderText += getString(R.string.Every) + xWeeks + getString(R.string.weeks);
                                    break;
                                case "everyXMonths":
                                    medTime = documentSnapshot.getString("medTime");
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xMonths = documentSnapshot.getString("everyXMonths");
                                    reminderText += getString(R.string.Every) + xMonths + getString(R.string.months);
                                    break;
                                case "moreTimes":
                                    String howTimes = documentSnapshot.getString("howTimesDay");
                                    reminderText += howTimes + getString(R.string.times_in_day);
                                    ArrayList<String> times = (ArrayList<String>) documentSnapshot.get("times");
                                    ArrayList<String> doses = (ArrayList<String>) documentSnapshot.get("doses");
                                    for (int i = 0; i < times.size(); i++) {
                                        if (i != times.size() - 1) {

                                            medListText += times.get(i) + " " + doses.get(i) + doseType[0] + ", ";
                                        } else {
                                            medListText += times.get(i) + " " + doses.get(i) + doseType[0];

                                        }
                                    }
                                    break;
                            }
                            medReminders.setText(reminderText);
                            medTimes.setText(medListText);
                            if (medInstructionStr == null) {
                                medInstruction.setVisibility(View.GONE);
                            } else {
                                medInstruction.setText(medInstructionStr);
                            }
                            if (medCount[0].equals("null")) {
                                medAmount.setVisibility(View.GONE);
                            } else {
                                amountEt.setCursorVisible(false);
                                amountEt.setText(medCount[0]);
                                doseTypeTV.setText(doseType[0]);
                                progressBar.setVisibility(View.GONE);
                                plusCount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int count = Integer.parseInt(medCount[0]);
                                        count++;
                                        if (count > 99999) count = 99999;
                                        medCount[0] = String.valueOf(count);
                                        amountEt.setText(String.valueOf(count));
                                        setNewCount(medCount[0]);

                                    }
                                });
                                minusCount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int count = Integer.parseInt(medCount[0]);
                                        count--;
                                        if (count < 0) count = 0;
                                        medCount[0] = String.valueOf(count);
                                        amountEt.setText(String.valueOf(count));
                                        setNewCount(medCount[0]);

                                    }
                                });
                                amountEt.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        medCount[0] = amountEt.getText().toString();
                                        setNewCount(medCount[0]);

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {

                                    }
                                });


                            }
                            if (medFoodStr == null) {
                                medFood.setVisibility(View.GONE);
                            } else {
                                String text;
                                if (medFoodStr.equals("fromEating")) {
                                    text = "from eating";
                                } else if (medFoodStr.equals("beforeEating")) {
                                    text = "before eating";
                                } else {
                                    text = "after eating";
                                }
                                medFood.setText(getString(R.string.you_should_take_it) + text);
                            }

                        } else {
                            progressBar.setVisibility(View.GONE);

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }

    private void setNewCount(String s) {
        CollectionReference med = db.collection("meds");

        Map<String, Object> data = new HashMap<>();
        data.put("medCount", Integer.parseInt(s));

        med.document(medId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("failure");
                    }
                });
    }

    private String formatTime(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return String.format("%02d:%02d", hours, minutes);
    }

    private void deleteVisit(String medId) {
        db.collection("meds").document(medId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userMeds").document(medId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        finish();
    }
}