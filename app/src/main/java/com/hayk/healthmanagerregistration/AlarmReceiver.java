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
import android.view.View;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import AddMedFragments.AlarmDates;

public class AlarmReceiver extends BroadcastReceiver {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String PREFS_NAME = "language_prefs";
    private static final String LANGUAGE_KEY = "language";


    @Override
    public void onReceive(Context context, Intent intent) {
        String language = getSavedLanguagePreference(context);
        setLocale(context, language, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String medId = intent.getStringExtra("medId");
        String medFrequency = intent.getStringExtra("medFrequency");
        isMedExist(context, medId, medFrequency);

    }


    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Context context, AlarmDates alarmDates) {
        if (context == null) {
            return;
        }
        db = FirebaseFirestore.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        calendar.set(Calendar.DAY_OF_MONTH, alarmDates.getDay());
        calendar.set(Calendar.MONTH, alarmDates.getMonth());
        calendar.set(Calendar.YEAR, alarmDates.getYear());
        calendar.set(Calendar.HOUR_OF_DAY, alarmDates.getHour());
        calendar.set(Calendar.MINUTE, alarmDates.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            if (alarmDates.getMedFrequency().equals("onceDay")
                    || alarmDates.getMedFrequency().equals("twiceDay")
                    || alarmDates.getMedFrequency().equals("moreTimes")) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                setNextAlarm(context, alarmDates.getMedId(), alarmDates.getMedFrequency());
                return;
            }
        }

        CollectionReference medsCollection = db.collection("requestCode");

        medsCollection.document("medRequestCode").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            long requestCode = documentSnapshot.getLong("code");
                            Intent intent = new Intent(context, AlarmReceiver.class);
                            intent.putExtra("medId", alarmDates.getMedId());

                            intent.putExtra("medFrequency", alarmDates.getMedFrequency());
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

        String hour = (alarmDates.getHour() < 10) ? "0" + alarmDates.getHour() : String.valueOf(alarmDates.getHour());
        String minute = (alarmDates.getMinute() < 10) ? "0" + alarmDates.getMinute() : String.valueOf(alarmDates.getMinute());
        String nextTime = hour + ":" + minute;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String formattedDay = String.format("%02d", dayOfMonth);
        String formattedMonth = String.format("%02d", month);
        String formattedYear = String.format("%04d", year);

        String nextDate = formattedDay + "/" + formattedMonth + "/" + formattedYear;

        setNextTimeTODB(nextTime, alarmDates.getMedId());
        setNextDateTODB(nextDate, alarmDates.getMedId());
    }

    private void isMedExist(Context context, String medId, String medFrequency) {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("userMeds")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String docName = document.getId();
                                if (docName.equals(medId)) {
                                    getDataForNotification(context, medId, medFrequency);
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

    private void setNextDateTODB(String nextDate, String medId) {
        CollectionReference med = db.collection("meds");

        Map<String, Object> data = new HashMap<>();
        data.put("nextDate", nextDate);

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


    private void setNextTimeTODB(String nextTime, String medId) {
        CollectionReference med = db.collection("meds");

        Map<String, Object> data = new HashMap<>();
        data.put("nextTime", nextTime);

        med.document(medId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                    }
                });
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

    private void getDataForNotification(Context context, String medId, String medFrequency) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            System.out.println(medFrequency);

                            String doseType = documentSnapshot.getString("doseType");
                            String doseCount = documentSnapshot.getString("doseCount");
                            String medName = documentSnapshot.getString("medName");
                            String nextTime = documentSnapshot.getString("nextTime");
                            String text;
                            switch (doseType) {
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

                            int count = 0;
                            if (doseType == null) {
                                text = context.getString(R.string.don_t_forget_to_take_your) + medName + context.getString(R.string.at) + nextTime;
                            } else {
                                if (medFrequency.equals("twiceDay")) {
                                    String firstDoseCount = documentSnapshot.getString("firstDoseCount");
                                    String secondDoseCount = documentSnapshot.getString("secondDoseCount");
                                    String medFirstTime = documentSnapshot.getString("medFirstTime");

                                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("H:m");

                                    try {
                                        Date date = inputFormat.parse(nextTime);
                                        nextTime = outputFormat.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (nextTime.equals(medFirstTime)) {
                                        doseCount = firstDoseCount;
                                    } else {
                                        doseCount = secondDoseCount;
                                    }
                                    count = Integer.parseInt(doseCount);
                                    text = context.getString(R.string.don_t_forget_to_take) + doseCount + " " + doseType + context.getString(R.string.of) + medName + context.getString(R.string.at) + nextTime;


                                } else if (medFrequency.equals("moreTimes")) {
                                    List<String> medTimes = (List<String>) documentSnapshot.get("times");
                                    List<String> medDoses = (List<String>) documentSnapshot.get("doses");
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("H:m");

//                                    try {
//                                        Date date = inputFormat.parse(nextTime);
//                                        nextTime = outputFormat.format(date);
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
                                    for (String t : medTimes) {
                                        if (t.equals(nextTime)) {
                                            doseCount = medDoses.get(medTimes.indexOf(t));
                                        }
                                    }
                                    try {
                                        count = Integer.parseInt(doseCount);
                                    } catch (NumberFormatException e) {
                                        count = 1;
                                    }

                                    text = context.getString(R.string.don_t_forget_to_take) + doseCount + " " + doseType + context.getString(R.string.of) + medName + context.getString(R.string.at) + nextTime;

                                } else {
                                    count = Integer.parseInt(doseCount);

                                    text = context.getString(R.string.don_t_forget_to_take) + doseCount + " " + doseType + context.getString(R.string.of) + medName + context.getString(R.string.at) + nextTime;
                                }
                            }

                            sendNotification(context, medId, medFrequency, text, count);


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

    private void sendNotification(Context context, String medId, String medFrequency, String notificationText, int count) {
        int requestCode = (int) System.currentTimeMillis();

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

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent snoozeIntent = new Intent(context, NotificationHandler.class);
        snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("medId", medId);
        snoozeIntent.putExtra("requestCode", requestCode);
        snoozeIntent.putExtra("text", notificationText);
        snoozeIntent.putExtra("doseCount", count);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, requestCode, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent skipIntent = new Intent(context, NotificationHandler.class);
        skipIntent.setAction("ACTION_SKIP");
        skipIntent.putExtra("medId", medId);
        skipIntent.putExtra("requestCode", requestCode);
        skipIntent.putExtra("text", notificationText);
        skipIntent.putExtra("doseCount", count);
        PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context, requestCode, skipIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent takeIntent = new Intent(context, NotificationHandler.class);
        takeIntent.setAction("ACTION_TAKE");
        takeIntent.putExtra("medId", medId);
        takeIntent.putExtra("requestCode", requestCode);
        takeIntent.putExtra("doseCount", count);
        takeIntent.putExtra("text", notificationText);
        PendingIntent takePendingIntent = PendingIntent.getBroadcast(context, requestCode, takeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(notificationText)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.logo, context.getString(R.string.skip), skipPendingIntent)
                        .addAction(R.drawable.logo, context.getString(R.string.take), takePendingIntent)
                        .addAction(R.drawable.logo, context.getString(R.string.snooze), snoozePendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, builder.build());
        setIsTake(medId);
        setNextAlarm(context, medId, medFrequency);
    }

    private void setIsTake(String medId) {
        CollectionReference medsCollection = db.collection("meds");
        HashMap<String, Object> data = new HashMap<>();
        data.put("isTake", false);
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

    public void setNextAlarm(Context context, String medId, String medFrequency) {
        db = FirebaseFirestore.getInstance();
        if (medFrequency.equals("onceDay")) {
            getDataForOnceDay(context, medId);
        } else if (medFrequency.equals("twiceDay")) {
            getDataForTwiceDay(context, medId);
        } else if (medFrequency.equals("moreTimes")) {
            getDataForMoreTimes(context, medId);
        } else if (medFrequency.equals("everyXDays")) {
            getDataForEveryXDays(context, medId);

        } else if (medFrequency.equals("everyXWeeks")) {
            getDataForEveryXWeeks(context, medId);

        } else if (medFrequency.equals("everyXMonths")) {
            getDataForEveryXMonths(context, medId);
        } else if (medFrequency.equals("specificDays")) {
            getDataForSpecificDays(context, medId);
        } else if (medFrequency.equals("everyOtherDay")) {
            getDataForEveryOtherDays(context, medId);
        }
    }


    private void getDataForEveryOtherDays(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");

                            setAlarmEveryOtherDay(context, medTime, medId, "onceDay");
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


    private void getDataForOnceDay(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");

                            setAlarmForOnceDay(context, medTime, medId, "onceDay");
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

    private void getDataForTwiceDay(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstTime = documentSnapshot.getString("medFirstTime");
                            String secondTime = documentSnapshot.getString("medSecondTime");
                            setAlarmForTwiceDay(context, firstTime, secondTime, medId, "twiceDay");
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

    private void getDataForMoreTimes(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> times = (List<String>) documentSnapshot.get("times");
                            setAlarmForMoreTimes(context, times, medId, "moreTimes");
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

    private void getDataForEveryXDays(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String everyXDays = documentSnapshot.getString("everyXDays");
                            setAlarmForEveryXDays(context, medTime, everyXDays, medId, "everyXDays");
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

    private void getDataForEveryXWeeks(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String everyXWeeks = documentSnapshot.getString("everyXWeeks");
                            setAlarmForEveryXWeeks(context, medTime, everyXWeeks, medId, "everyXWeeks");
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

    private void getDataForEveryXMonths(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String everyXMonths = documentSnapshot.getString("everyXMonths");
                            setAlarmForEveryXMonths(context, medTime, everyXMonths, medId, "everyXMonths");
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

    private void getDataForSpecificDays(Context context, String medId) {
        CollectionReference medsCollection = db.collection("meds");
        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            List<String> daysOfWeek1 = (List<String>) documentSnapshot.get("daysOfWeek");
                            String[] daysOfWeek = daysOfWeek1.toArray(new String[0]);
                            setAlarmWithSpecificDaysOfWeek(context, medTime, medId, "specificDays", daysOfWeek);
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

    private void setAlarmForOnceDay(Context context, String time, String medId, String medFrequency) {
        LocalDate currentDate = LocalDate.now();
        String[] parts = time.split(":");
        int hour;
        int minute;

        String hourStr = parts[0];
        hour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        minute = Integer.parseInt(minuteStr);

        AlarmDates alarmDates = new AlarmDates(currentDate.getYear(), currentDate.getMonth().getValue() - 1, currentDate.getDayOfMonth(), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmForTwiceDay(Context context, String firstTime, String secondTime, String medId, String medFrequency) {
        LocalDate currentDate = LocalDate.now();
        String[] parts = firstTime.split(":");
        int firstHour;
        int firstMinute;

        String hourStr = parts[0];
        firstHour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        firstMinute = Integer.parseInt(minuteStr);
        String[] parts1 = secondTime.split(":");
        int secondHour;
        int secondMinute;

        String secondHourStr = parts1[0];
        secondHour = Integer.parseInt(secondHourStr);

        String secondMinuteStr = parts1[1];
        secondMinute = Integer.parseInt(secondMinuteStr);
        Calendar secondTimeCalendar = Calendar.getInstance();
        secondTimeCalendar.setTimeInMillis(System.currentTimeMillis());

        secondTimeCalendar.set(Calendar.HOUR_OF_DAY, secondHour);
        secondTimeCalendar.set(Calendar.MINUTE, secondMinute);
        secondTimeCalendar.set(Calendar.SECOND, 0);
        Calendar firstTimeCalendar = Calendar.getInstance();
        firstTimeCalendar.setTimeInMillis(System.currentTimeMillis());

        firstTimeCalendar.set(Calendar.HOUR_OF_DAY, firstHour);
        firstTimeCalendar.set(Calendar.MINUTE, firstMinute);
        firstTimeCalendar.set(Calendar.SECOND, 0);
        int hour;
        int minute;
        if (secondTimeCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            hour = firstHour;
            minute = firstMinute;
            System.out.println("setAlarm arajin if " + hour + ":" + minute);
        } else if (firstTimeCalendar.getTimeInMillis() <= System.currentTimeMillis()) {

            hour = secondHour;
            minute = secondMinute;
            System.out.println("setAlarm erkrord if " + hour + ":" + minute);
        } else {

            hour = firstHour;
            minute = firstMinute;
            System.out.println("setAlarm else " + hour + ":" + minute);
        }

        AlarmDates alarmDates = new AlarmDates(currentDate.getYear(), currentDate.getMonth().getValue() - 1, currentDate.getDayOfMonth(), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmForMoreTimes(Context context, List<String> times, String medId, String medFrequency) {
        for (String time : times) {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            LocalDate currentDate = LocalDate.now();

            LocalDateTime alarmDateTime = LocalDateTime.of(currentDate, LocalTime.of(hour, minute));

            AlarmDates alarmDates;
            if (!LocalDateTime.now().isAfter(alarmDateTime)) {
                alarmDates = new AlarmDates(
                        alarmDateTime.getYear(),
                        alarmDateTime.getMonthValue() - 1,
                        alarmDateTime.getDayOfMonth(),
                        alarmDateTime.getHour(),
                        alarmDateTime.getMinute(),
                        medId,
                        medFrequency
                );
                AlarmReceiver alarmReceiver = new AlarmReceiver();
                alarmReceiver.setAlarm(context, alarmDates);
                break;
            }
            parts = times.get(0).split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
            alarmDateTime = LocalDateTime.of(currentDate, LocalTime.of(hour, minute));
            alarmDates = new AlarmDates(
                    alarmDateTime.getYear(),
                    alarmDateTime.getMonthValue() - 1,
                    alarmDateTime.getDayOfMonth(),
                    alarmDateTime.getHour(),
                    alarmDateTime.getMinute(),
                    medId,
                    medFrequency
            );
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.setAlarm(context, alarmDates);

        }
    }

    private void setAlarmForEveryXDays(Context context, String time, String everyXDays, String medId, String medFrequency) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();


        LocalDate futureDate = currentDate.plusDays(Integer.parseInt(everyXDays));

        AlarmDates alarmDates = new AlarmDates(
                futureDate.getYear(),
                futureDate.getMonthValue() - 1,
                futureDate.getDayOfMonth(),
                hour,
                minute,
                medId,
                medFrequency
        );

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmForEveryXWeeks(Context context, String time, String everyXWeeks, String medId, String medFrequency) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();

        LocalDate futureDate = currentDate.plusDays(Integer.parseInt(everyXWeeks) * 7L);

        AlarmDates alarmDates = new AlarmDates(
                futureDate.getYear(),
                futureDate.getMonthValue() - 1,
                futureDate.getDayOfMonth(),
                hour,
                minute,
                medId,
                medFrequency
        );

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmForEveryXMonths(Context context, String time, String everyXMonths, String medId, String medFrequency) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();
        int monthsToAdd = Integer.parseInt(everyXMonths);

        LocalDate futureDate = currentDate.plusMonths(monthsToAdd);

        while (currentDate.getDayOfMonth() != futureDate.getDayOfMonth()) {
            futureDate = futureDate.plusDays(-1);
        }

        AlarmDates alarmDates = new AlarmDates(
                futureDate.getYear(),
                futureDate.getMonthValue() - 1,
                futureDate.getDayOfMonth(),
                hour,
                minute,
                medId,
                medFrequency
        );

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmWithSpecificDaysOfWeek(Context context, String time, String medId, String medFrequency, String[] specificDaysOfWeek) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        int temp = 0;
        boolean found = false;


        int dayOfWeekNow = currentDate.getDayOfWeek().getValue();
        for (String day : specificDaysOfWeek) {
            int dayValue = DayOfWeek.valueOf(day.toUpperCase()).getValue();
            if (dayValue > dayOfWeekNow) {
                temp = dayValue - dayOfWeekNow;
                found = true;
                break;
            } else if (dayValue == dayOfWeekNow) {
                int hourNow = currentTime.getHour();
                int minuteNow = currentTime.getMinute();
                if (hourNow < hour) {
                    found = true;
                    temp = 0;
                    break;
                } else if (hourNow == hour) {

                    if (minuteNow < minute) {
                        found = true;
                        temp = 0;
                        break;
                    }
                }

            }

        }
        if (!found) {
            temp = 7 - dayOfWeekNow + DayOfWeek.valueOf(specificDaysOfWeek[0].toUpperCase()).getValue();
        }
        LocalDate futureDate = currentDate.plusDays(temp);

        AlarmDates alarmDates = new AlarmDates(
                futureDate.getYear(),
                futureDate.getMonthValue() - 1,
                futureDate.getDayOfMonth(),
                hour,
                minute,
                medId,
                medFrequency
        );
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void setAlarmEveryOtherDay(Context context, String medTime, String medId, String medFrequency) {
        String[] parts = medTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();


        LocalDate futureDate = currentDate.plusDays(2);

        AlarmDates alarmDates = new AlarmDates(
                futureDate.getYear(),
                futureDate.getMonthValue() - 1,
                futureDate.getDayOfMonth(),
                hour,
                minute,
                medId,
                medFrequency
        );

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
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
