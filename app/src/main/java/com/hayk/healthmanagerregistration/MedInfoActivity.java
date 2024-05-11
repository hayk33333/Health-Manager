package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MedInfoActivity extends AppCompatActivity {
    private TextView medName, medForm, medReminders, medTimes, medAmount, medFood, medInstruction;
    private ImageView back, delete;
    private FirebaseFirestore db;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);
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
        String medId = getIntent().getStringExtra("medId");
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
                            String medCount = String.valueOf(documentSnapshot.getLong("medCount"));
                            String doseType = documentSnapshot.getString("doseType");
                            String medFoodStr = documentSnapshot.getString("medWithFood");
                            medName.setText(medNameStr);
                            medForm.setText(getString(R.string.med_form) + medFormStr);
                            String reminderText = getString(R.string.reminders);
                            String medListText = getString(R.string.medication_list);

                            switch (medRemindersStr) {
                                case "onceDay":
                                    String medTime = documentSnapshot.getString("medTime");
                                    String doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + doseCount;
                                    reminderText += "Once a day";
                                    break;
                                case "twiceDay":
                                    reminderText += "Twice a day";
                                    break;
                                case "everyOtherDay":

                                    reminderText += "Every other day";
                                    break;
                                case "everyXDays":
                                    String xDays = documentSnapshot.getString("everyXDays");
                                    reminderText += "Every " + xDays + " days";
                                    break;
                                case "specificDays":
                                    ArrayList<String> days = (ArrayList<String>) documentSnapshot.get("daysOfWeek");
                                    reminderText += "On " ;
                                    for (String day : days) {
                                        reminderText += day + " ";
                                    }
                                    break;
                                case "everyXWeeks":
                                    String xWeeks = documentSnapshot.getString("everyXWeeks");
                                    reminderText += "Every " + xWeeks + " weeks";
                                    break;
                                case "everyXMonths":
                                    String xMonths = documentSnapshot.getString("everyXMonths");
                                    reminderText += "Every " + xMonths + " months";
                                    break;
                            }
                            medReminders.setText(reminderText);
                            if (medInstructionStr == null) {
                                medInstruction.setVisibility(View.GONE);
                            } else {
                                medInstruction.setText(medInstructionStr);
                            }
                            if (medCount == "null") {
                                medAmount.setVisibility(View.GONE);
                            } else {

                                medAmount.setText(getString(R.string.amount_of_med) + medCount + doseType);
                            }
                            if (medFoodStr == null) {
                                medFood.setVisibility(View.GONE);
                            } else {
                                String text;
                                if (medFoodStr.equals("fromEating")){
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