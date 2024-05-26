package com.hayk.healthmanagerregistration;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiverVisit extends BroadcastReceiver {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static final String PREFS_NAME = "language_prefs";
    private static final String LANGUAGE_KEY = "language";


    @Override
    public void onReceive(Context context, Intent intent) {
        String language = getSavedLanguagePreference(context);
        setLocale(context, language, false);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String visitId = intent.getStringExtra("visitId");
        String type = intent.getStringExtra("type");
        String count = intent.getStringExtra("count");
        isVisitExist(context, visitId, type, count);


    }

    private void isVisitExist(Context context, String visitId, String type, String count) {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("userVisits")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String docName = document.getId();
                                if (docName.equals(visitId)) {
                                    sendNotificationVisit(context, visitId, type, count);
                                    break;
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Context context, String type, String count, String time, String year, String month, String day, String visitId) {
        if (context == null) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String[] parts = time.split(":");
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

        switch (type) {
            case "minute(s)":
            case "минута(ы)":
                type = context.getResources().getString(R.string.minute_s);
                calendar.add(Calendar.MINUTE, -Integer.valueOf(count));
                break;
            case "hour(s)":
            case "час(ов)":
                type = context.getResources().getString(R.string.hour_s);
                calendar.add(Calendar.HOUR, -Integer.valueOf(count));
                break;
            case "day(s)":
            case "день(дней)":
                type = context.getResources().getString(R.string.day_s);

                calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(count));
                break;
            case "week(s)":
            case "неделя(и)":
                type = context.getResources().getString(R.string.week_s);
                calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(count) * 7);
                break;
            case "month(s)":
            case "месяц(ы)":
                type = context.getResources().getString(R.string.month_s);
                calendar.add(Calendar.MONTH, -Integer.valueOf(count));
                break;
            case "year(s)":
            case "год(годы)":
                type = context.getResources().getString(R.string.year_s);
                calendar.add(Calendar.YEAR, -Integer.valueOf(count));
                break;
        }


        Intent intent = new Intent(context, AlarmReceiverVisit.class);
        intent.putExtra("visitId", visitId);
        intent.putExtra("type", type);
        intent.putExtra("count", count);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


    }

    private void sendNotificationVisit(Context context, String visitId, String type, String count) {
        CollectionReference medsCollection = db.collection("visits");

        medsCollection.document(visitId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String doctorName = documentSnapshot.getString("doctorName");
                            String hospitalName = documentSnapshot.getString("hospitalName");
                            String text = null;
                            if (doctorName == null) {
                                doctorName = "";
                            }
                            if (hospitalName == null) {
                                hospitalName = "";
                            }
                            if (doctorName.isEmpty() && hospitalName.isEmpty()) {
                                text = context.getString(R.string.you_should_visit_the_hospital_in) + count + " " + type + ".";
                            } else if (!hospitalName.isEmpty() && doctorName.isEmpty()) {
                                text = context.getString(R.string.you_should_visit) + hospitalName + context.getString(R.string.hospital) + count + " " + type + ".";
                            } else if (!doctorName.isEmpty() && hospitalName.isEmpty()) {
                                text = context.getString(R.string.you_should_visit) + doctorName + context.getString(R.string.at_the_hospital) + count + " " + type + ".";
                            } else {
                                text = context.getString(R.string.you_should_visit) + doctorName + context.getString(R.string.in) + hospitalName + context.getString(R.string.hospital) + count + " " + type + ".";
                            }
                            PendingIntent pendingIntent;
                            Intent intent = new Intent(context, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            }
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
                                            .setContentText(text)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true);


                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
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

    private void saveLanguagePreference(Context context, String languageCode) {
        context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(LANGUAGE_KEY, languageCode)
                .apply();
    }

    private String getSavedLanguagePreference(Context context) {
        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "en");
    }

    private void setLocale(Context context, String languageCode, boolean shouldRestartActivity) {
        String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
        if (!currentLanguage.equals(languageCode)) {
            LanguageManager.setLocale(context, new Locale(languageCode));
            saveLanguagePreference(context, languageCode);
//            if (shouldRestartActivity) {
//
//            }
        }
    }
}
