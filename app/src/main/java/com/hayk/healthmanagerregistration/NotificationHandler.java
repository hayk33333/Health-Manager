package com.hayk.healthmanagerregistration;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NotificationHandler extends BroadcastReceiver {
    FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = FirebaseFirestore.getInstance();
        String action = intent.getAction();
        int requestCode = intent.getIntExtra("requestCode", 0);
        Toast.makeText(context, String.valueOf(requestCode), Toast.LENGTH_SHORT).show();
        String medId = intent.getStringExtra("medId");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(requestCode);

        if ("ACTION_SNOOZE".equals(action)) {
            handleSnoozeAction(context, medId);
        } else if ("ACTION_SKIP".equals(action)) {
            handleSkipAction(context, medId);
        } else if ("ACTION_TAKE".equals(action)) {
            handleTakeAction(context, medId);
        }
    }

    private void handleSnoozeAction(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medName = documentSnapshot.getString("medName");
                            Toast.makeText(context, medName, Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("med does not exists");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("error to read from db");
                    }
                });

    }
    private void handleTakeAction(Context context, String medId) {
        System.out.println("take");
        Toast.makeText(context, "take", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, medId, Toast.LENGTH_SHORT).show();


    }
    private void handleSkipAction(Context context, String medId) {
        Toast.makeText(context, "skip", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, medId, Toast.LENGTH_SHORT).show();

        System.out.println("skip");

    }
    private void updateMedCount(Context context, String medId, String medFrequency, int doseCount) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            try {
                                long count = documentSnapshot.getLong("medCount");

                                count -= doseCount;
                                if (count < 0) {
                                    count = 0;
                                }
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("medCount", String.valueOf(count));
                                medsCollection.document(medId).update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Error updating document: " + e);
                                            }
                                        });

                            } catch (Exception e) {
                            }


                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error reading document: " + e);
                    }
                });
    }
}
