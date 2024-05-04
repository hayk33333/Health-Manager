package AddMedFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.AlarmReceiver;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MedAdditionalInformationFragment extends Fragment {
    private ImageView back, firstMark, secondMark, thirdMark;
    private AddMedicationActivity addMedicationActivity;
    private RelativeLayout addInstruction, addCount, medWithEating;
    private String documentId;
    private Button save;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        save = view.findViewById(R.id.save);
        back = view.findViewById(R.id.back);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firstMark = view.findViewById(R.id.first_mark);
        secondMark = view.findViewById(R.id.second_mark);
        thirdMark = view.findViewById(R.id.third_mark);
        addCount = view.findViewById(R.id.add_count);
        addInstruction = view.findViewById(R.id.add_instruction);
        documentId = getArguments().getString("documentId");
        medWithEating = view.findViewById(R.id.med_with_eating);
        addMedicationActivity = (AddMedicationActivity) requireActivity();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIsTake();
                setMedFirstAlarm();
                saveMedToDb();
            }
        });

        addCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedCountFragment();
            }
        });
        addInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedInstructionFragment();

            }
        });
        medWithEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedWithFoodFragment();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedAdditionalInformationFragment();
            }
        });

    }
    private void setIsTake() {
        CollectionReference medsCollection = db.collection("meds");
        HashMap<String, Object> data = new HashMap<>();
        data.put("isTake", false);
        medsCollection.document(documentId).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {

            }
        });
    }

    private void setMedFirstAlarm() {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medFrequency = documentSnapshot.getString("medFrequency");
                            if (medFrequency.equals("onceDay")) {
                                getDataForOnceDay(getActivity());
                            } else if (medFrequency.equals("twiceDay")) {
                                getDataForTwiceDay(getActivity());
                            } else if (medFrequency.equals("moreTimes")) {
                                getDataForMoreTimes(getActivity());
                            } else if (medFrequency.equals("everyXDays")) {
                                getDataForEveryXDays(getActivity());

                            } else if (medFrequency.equals("everyXWeeks")) {
                                getDataForEveryXWeeks(getActivity());

                            } else if (medFrequency.equals("everyXMonths")) {
                                getDataForEveryXMonths(getActivity());

                            } else if (medFrequency.equals("specificDays")) {
                                getDataForSpecificDays(getActivity());
                            } else if (medFrequency.equals("everyOtherDay")) {
                                getDataForEveryOtherDays(getActivity());
                            }
                            getActivity().finish();

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


    private void saveMedToDb() {
        String userId = firebaseUser.getUid();

        CollectionReference usersCollection = db.collection("users");

        DocumentReference documentReference = usersCollection.document(userId);

        CollectionReference medsCollection = documentReference.collection("userMeds");

        DocumentReference newDocumentRef = medsCollection.document(documentId);

        newDocumentRef.set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Документ успешно создан в коллекции 'users/meds'! Документ ID: " + documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при создании документа в коллекции 'users/meds': " + e.getMessage());
                    }
                });
    }

    public void setCheckMarks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.contains("medWithFood")) {
                                thirdMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                thirdMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }
                            if (documentSnapshot.contains("medRemindCount")) {
                                secondMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                secondMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }
                            if (documentSnapshot.contains("medInstruction")) {
                                firstMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                firstMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }

                        } else {
                            firstMark.setVisibility(View.GONE);
                            secondMark.setVisibility(View.GONE);
                            thirdMark.setVisibility(View.GONE);
                            System.out.println("Документ не существует");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.printf("Ошибка при чтении из базы данных");
                    }
                });
    }

    private void getDataForOnceDay(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            setAlarmForOnceDay(context, medTime, documentId, "onceDay");
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

    private void getDataForTwiceDay(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstTime = documentSnapshot.getString("medFirstTime");
                            String secondTime = documentSnapshot.getString("medSecondTime");
                            setAlarmForTwiceDay(context, firstTime, secondTime, "twiceDay");
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

    private void setAlarmForTwiceDay(Context context, String firstTime, String secondTime, String medFrequency) {
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
        AlarmDates alarmDates = new AlarmDates(currentDate.getYear(), currentDate.getMonth().getValue() - 1, currentDate.getDayOfMonth(), hour, minute, documentId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void getDataForEveryXDays(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            setAlarmForEveryXDays(context, medTime, documentId, "everyXDays", year, month, day);
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

    private void setAlarmForEveryXDays(Context context, String time, String medId, String medFrequency, String year, String month, String day) {
        String[] parts = time.split(":");
        int hour;
        int minute;

        String hourStr = parts[0];
        hour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        minute = Integer.parseInt(minuteStr);

        AlarmDates alarmDates = new AlarmDates(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void getDataForEveryXWeeks(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            setAlarmForEveryXWeeks(context, medTime, documentId, "everyXWeeks", year, month, day);
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

    private void setAlarmForEveryXWeeks(Context context, String time, String medId, String medFrequency, String year, String month, String day) {
        String[] parts = time.split(":");
        int hour;
        int minute;

        String hourStr = parts[0];
        hour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        minute = Integer.parseInt(minuteStr);

        AlarmDates alarmDates = new AlarmDates(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void getDataForEveryXMonths(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            setAlarmForEveryXMonths(context, medTime, documentId, "everyXMonths", year, month, day);
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

    private void setAlarmForEveryXMonths(Context context, String time, String medId, String medFrequency, String year, String month, String day) {
        String[] parts = time.split(":");
        int hour;
        int minute;

        String hourStr = parts[0];
        hour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        minute = Integer.parseInt(minuteStr);

        AlarmDates alarmDates = new AlarmDates(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }

    private void getDataForMoreTimes(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> times = (List<String>) documentSnapshot.get("times");
                            setAlarmForMoreTimes(context, times, documentId, "moreTimes");
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

    private void getDataForSpecificDays(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            List<String> daysOfWeek1 = (List<String>) documentSnapshot.get("daysOfWeek");
                            String[] daysOfWeek = daysOfWeek1.toArray(new String[0]);

                            setAlarmWithSpecificDaysOfWeek(context, medTime, documentId, "specificDays", daysOfWeek);
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
                System.out.println("1");
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

                    if (minuteNow < minute){
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


    private void getDataForEveryOtherDays(Context context) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medTime = documentSnapshot.getString("medTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            setAlarmEveryOtherDay(context, medTime, documentId, "everyOtherDay", year, day, month);
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

    private void setAlarmEveryOtherDay(Context context, String medTime, String medId, String medFrequency, String year, String day, String month) {
        String[] parts = medTime.split(":");
        int hour;
        int minute;

        String hourStr = parts[0];
        hour = Integer.parseInt(hourStr);

        String minuteStr = parts[1];
        minute = Integer.parseInt(minuteStr);

        AlarmDates alarmDates = new AlarmDates(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), hour, minute, medId, medFrequency);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(context, alarmDates);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_additional_information, container, false);
    }
}