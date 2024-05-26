package com.hayk.healthmanagerregistration;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
        int doseCount = intent.getIntExtra("doseCount", 0);
        String text = intent.getStringExtra("text");
        String medId = intent.getStringExtra("medId");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(requestCode);

        if ("ACTION_SNOOZE".equals(action)) {
            handleSnoozeAction(context, text,medId,doseCount);
        } else if ("ACTION_SKIP".equals(action)) {
            handleSkipAction(context, medId);
        } else if ("ACTION_TAKE".equals(action)) {
            handleTakeAction(context, medId, doseCount);
        }
    }

    private void handleSkipAction(Context context, String medId) {
        setIsTake(medId, false);
        System.out.println("skip");

    }

    private void handleSnoozeAction(Context context,String text,String medId, int count) {
        AlarmReceiverNotification alarm = new AlarmReceiverNotification();
        alarm.setAlarm(context, 10, text, "MED_SNOOZE", medId,count);
    }

    private void handleTakeAction(Context context, String medId, int doseCount) {
        System.out.println("take");

        updateMedCount(context, medId, doseCount);
        //setIsTake(medId, true);


    }

    private void setIsTake(String medId, boolean isTake) {
        CollectionReference medsCollection = db.collection("meds");
        HashMap<String, Object> data = new HashMap<>();
        data.put("isTake", isTake);
        medsCollection.document(medId).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void updateMedCount(Context context, String medId, int doseCount) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            try {
                                long count = documentSnapshot.getLong("medCount");
                                long remindCount = documentSnapshot.getLong("medRemindCount");


                                count -= doseCount;
                                if (count < 0) {
                                    count = 0;
                                }
                                if (count <= remindCount) {

                                    getNotificationText(context, medId, (int) (remindCount));
                                }
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("medCount", count);

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
                                System.out.println("catch" + e.getMessage());
                            }


                        } else {
                            System.out.println("chka");

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

    private void getNotificationText(Context context, String medId, int count) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String doseType = documentSnapshot.getString("doseType");
                            String medName = documentSnapshot.getString("medName");
                            switch (doseType){
                            case "pill(s)":
                                doseType = context.getResources().getString(R.string.pill_s);
                                break;
                            case "mL":
                                doseType = context.getResources().getString(R.string.ml);
                                break;
                            case "Syringe(s)":
                                doseType = context.getResources().getString(R.string.syringe_s);
                                break;

                            case "Unit":
                                doseType = context.getResources().getString(R.string.unit);
                                break;
                            case "Ampules(s)":
                                doseType = context.getResources().getString(R.string.ampules_s);
                                break;
                            case "Vial(s)":
                                doseType = context.getResources().getString(R.string.vial_s);
                                break;
                            case "Cup(s)":
                                doseType = context.getResources().getString(R.string.cup_s);
                                break;
                            case "Drop(s)":
                                doseType = context.getResources().getString(R.string.drop_s);
                                break;
                            case "Packet(s)":
                                doseType = context.getResources().getString(R.string.packet_s);
                                break;
                            case "Gram(s)":
                                doseType = context.getResources().getString(R.string.gram_s);
                                break;
                            case "Tablespoon(s)":
                                doseType = context.getResources().getString(R.string.tablespoon_s);
                                break;
                            case "Teaspoon(s)":
                                doseType = context.getResources().getString(R.string.teaspoon_s);
                                break;



                        }

                        String text = context.getString(R.string.only) + count + " " + doseType + context.getString(R.string.left_of) + medName + context.getString(R.string.it_s_time_to_stock_up);
                            AlarmReceiverNotification alarm = new AlarmReceiverNotification();
                            alarm.setAlarm(context, 10, text, "MED_RUN_OUT", medId, count);


                        } else {
                            System.out.println("chka");

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
