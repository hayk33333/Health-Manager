package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class AlarmReceiverNotification extends BroadcastReceiver {
    FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra("text");
        String action = intent.getStringExtra("action");
        String medId = intent.getStringExtra("medId");
        int count = intent.getIntExtra("count",0);
        if (action.equals("MED_RUN_OUT")) {
            sendNotificationForMedRunOut(context, text);
        } else if (action.equals("MED_SNOOZE")) {
            sendNotificationForMedSnooze(context,text,medId,count);
        }

    }



    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Context context, int minute, String text, String action, String medId, int count) {
        db = FirebaseFirestore.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.MINUTE, minute);



        CollectionReference medsCollection = db.collection("requestCode");

        medsCollection.document("medRequestCode").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            long requestCode = documentSnapshot.getLong("code");
                            Intent intent = new Intent(context, AlarmReceiverNotification.class);
                            intent.putExtra("text", text);
                            intent.putExtra("action", action);
                            intent.putExtra("medId",medId);
                            intent.putExtra("count",count);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                            requestCode++;
                            addNewRequestCodeTODb(requestCode);
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

    private void sendNotificationForMedRunOut(Context context, String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    private void sendNotificationForMedSnooze(Context context, String text, String medId, int count) {
        int requestCode = (int) System.currentTimeMillis();

        System.out.println(requestCode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        Intent snoozeIntent = new Intent(context, NotificationHandler.class);
        snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("medId", medId);
        snoozeIntent.putExtra("requestCode", requestCode);
        snoozeIntent.putExtra("text", text);
        snoozeIntent.putExtra("doseCount", count);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, requestCode, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent skipIntent = new Intent(context, NotificationHandler.class);
        skipIntent.setAction("ACTION_SKIP");
        skipIntent.putExtra("medId", medId);
        skipIntent.putExtra("requestCode", requestCode);
        skipIntent.putExtra("text", text);
        skipIntent.putExtra("doseCount", count);


        PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context, requestCode, skipIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent takeIntent = new Intent(context, NotificationHandler.class);
        takeIntent.setAction("ACTION_TAKE");
        takeIntent.putExtra("medId", medId);
        takeIntent.putExtra("requestCode", requestCode);
        takeIntent.putExtra("doseCount", count);
        takeIntent.putExtra("text", text);


        PendingIntent takePendingIntent = PendingIntent.getBroadcast(context, requestCode, takeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(text)
                        .addAction(R.drawable.logo, context.getString(R.string.skip), skipPendingIntent)
                        .addAction(R.drawable.logo, context.getString(R.string.take), takePendingIntent)
                        .addAction(R.drawable.logo, context.getString(R.string.snooze), snoozePendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, builder.build());
    }
    private void addNewRequestCodeTODb(long requestCode) {
        CollectionReference medsCollection = db.collection("requestCode");

        HashMap<String, Integer> data = new HashMap<>();
        data.put("code", (int) requestCode);

        medsCollection
                .document("medRequestCode")
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Значение ' успешно добавлено в коллекцию 'meds'!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении значения '' в коллекцию 'meds': " + e.getMessage());
                    }
                });
    }
}
