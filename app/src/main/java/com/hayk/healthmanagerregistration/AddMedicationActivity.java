package com.hayk.healthmanagerregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import AddMedFragments.AddMedCountFragment;
import AddMedFragments.AddMedWithFoodFragment;
import AddMedFragments.DaysOfWeekFragment;
import AddMedFragments.EveryXHoursFragment;
import AddMedFragments.MedAddInstructionFragment;
import AddMedFragments.MedAdditionalInformationFragment;
import AddMedFragments.MedChooseFirstDayFragment;
import AddMedFragments.MedEveryXDaysFragment;
import AddMedFragments.MedEveryXMonths;
import AddMedFragments.MedEveryXWeeks;
import AddMedFragments.MedFirstDoseTimeFragment;
import AddMedFragments.MedFormFragment;
import AddMedFragments.MedFrequencyFragment;
import AddMedFragments.MedFrequencyInDayFragment;
import AddMedFragments.MedHowTimesDayFragment;
import AddMedFragments.MedNameFragment;
import AddMedFragments.MedReviewRemindersFragment;
import AddMedFragments.MedSecondDoseTimeFragment;
import AddMedFragments.MedTimeFragment;

public class AddMedicationActivity extends AppCompatActivity {
    TextView message;
    ImageView icon;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ProgressBar progressBar;
    String documentId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        message = findViewById(R.id.med_activity_message);
        icon = findViewById(R.id.icon);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        createMedsCollection();
        showMedNameFragment();


    }
    public void createMedsCollection() {
        CollectionReference medsCollection = db.collection("meds");

        DocumentReference newDocumentRef = medsCollection.document();
        documentId = newDocumentRef.getId();

        newDocumentRef.set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Коллекция 'meds' успешно создана! Документ ID: " + documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при создании коллекции 'meds': " + e.getMessage());
                    }
                });
    }



    public void showMedNameFragment() {
        progressBar.setProgress(10);
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedNameFragment medNameFragment = new MedNameFragment();
        medNameFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medNameFragment)
                .commit();
    }

    public void showMedFormFragment() {
        progressBar.setProgress(20);
        icon.setImageResource(R.drawable.med_form);
        message.setText(R.string.what_form_is_the_med);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedFormFragment medFormFragment = new MedFormFragment();
        medFormFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFormFragment)
                .commit();
    }
    public void showMedFrequencyFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedFrequencyFragment medFrequencyFragment = new MedFrequencyFragment();
        medFrequencyFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFrequencyFragment)
                .commit();

    }
    public void showMedFrequencyInDayFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedFrequencyInDayFragment medFrequencyInDayFragment = new MedFrequencyInDayFragment();
        medFrequencyInDayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFrequencyInDayFragment)
                .commit();
    }
    public void showDaysOfWeekFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.on_which_days_s_do_you_need_to_take_the_med);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        DaysOfWeekFragment daysOfWeekFragment = new DaysOfWeekFragment();
        daysOfWeekFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, daysOfWeekFragment)
                .commit();
    }
    public void showMedTimeFragment() {
        progressBar.setProgress(60);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.when_do_you_need_to_take_dose);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedTimeFragment medTimeFragment = new MedTimeFragment();
        medTimeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medTimeFragment)
                .commit();
    }
    public void showHowTimesDayFragment() {
        progressBar.setProgress(60);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.how_many_times_day);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedHowTimesDayFragment medHowTimesDayFragment = new MedHowTimesDayFragment();
        medHowTimesDayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medHowTimesDayFragment)
                .commit();
    }
    public void showMedFirstDoseTimeFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.when_do_you_need_to_take_the_first_dose);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedFirstDoseTimeFragment medFirstDoseTimeFragment = new MedFirstDoseTimeFragment();
        medFirstDoseTimeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFirstDoseTimeFragment)
                .commit();
    }
    public void showMedSecondDoseTimeFragment() {
        progressBar.setProgress(60);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.when_do_you_need_to_take_the_second_dose);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedSecondDoseTimeFragment medSecondDoseTimeFragment = new MedSecondDoseTimeFragment();
        medSecondDoseTimeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medSecondDoseTimeFragment)
                .commit();
    }
    public void showEveryXHoursFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.set_hours_interval);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        EveryXHoursFragment everyXHoursFragment = new EveryXHoursFragment();
        everyXHoursFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, everyXHoursFragment)
                .commit();
    }
    public void showMedChooseDayFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.when_do_you_need_to_take_the_next_dose);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedChooseFirstDayFragment medEveryOtherDayFragment = new MedChooseFirstDayFragment();
        medEveryOtherDayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medEveryOtherDayFragment)
                .commit();
    }
    public void showMedEveryXDaysFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.set_days_interval);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedEveryXDaysFragment medEveryXDaysFragment = new MedEveryXDaysFragment();
        medEveryXDaysFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medEveryXDaysFragment)
                .commit();
    }
    public void showMedEveryXWeeksFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.set_weeks_interval);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedEveryXWeeks medEveryXWeeksFragment = new MedEveryXWeeks();
        medEveryXWeeksFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medEveryXWeeksFragment)
                .commit();
    }
    public void showMedEveryXMonthsFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.set_months_interval);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedEveryXMonths medEveryXMonths = new MedEveryXMonths();
        medEveryXMonths.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medEveryXMonths)
                .commit();
    }
    public void showMedReviewRemindersFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.review_your_planned_reminders);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedReviewRemindersFragment medReviewRemindersFragment = new MedReviewRemindersFragment();
        medReviewRemindersFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medReviewRemindersFragment)
                .commit();
    }
    public void showMedAdditionalInformationFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.also_add_info);
        message.setText(R.string.what_also_would_you_like_to_add);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedAdditionalInformationFragment medAdditionalInformationFragment = new MedAdditionalInformationFragment();
        medAdditionalInformationFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medAdditionalInformationFragment)
                .commit();
    }
    public void showAddMedCountFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.how_much_many_do_you_have_left);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        AddMedCountFragment addMedCountFragment = new AddMedCountFragment();
        addMedCountFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, addMedCountFragment)
                .commit();
    }
    public void showAddMedWithFoodFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.med_with_food_icon);
        message.setText(R.string.should_this_be_taken_with_food);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        AddMedWithFoodFragment addMedWithFoodFragment = new AddMedWithFoodFragment();
        addMedWithFoodFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, addMedWithFoodFragment)
                .commit();
    }
    public void showAddMedInstructionFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.instruction_icon);
        message.setText(R.string.add_med_instruction);
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        MedAddInstructionFragment medAddInstructionFragment = new MedAddInstructionFragment();
        medAddInstructionFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medAddInstructionFragment)
                .commit();
    }



    public void hideMedFormFragment() {
        progressBar.setProgress(10);
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        MedFormFragment medFormFragment = (MedFormFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFormFragment != null && !medFormFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFormFragment).commit();
        }

    }

    public void hideMedFrequencyFragment() {
        progressBar.setProgress(20);
        icon.setImageResource(R.drawable.med_form);
        message.setText(R.string.what_form_is_the_med);
        MedFrequencyFragment medFrequencyFragment = (MedFrequencyFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFrequencyFragment != null && !medFrequencyFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFrequencyFragment).commit();
        }
    }

    public void hideMedFrequencyInDayFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedFrequencyInDayFragment medFrequencyInDayFragment = (MedFrequencyInDayFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFrequencyInDayFragment != null && !medFrequencyInDayFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFrequencyInDayFragment).commit();
        }
    }
    public void hideDaysOfWeekFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        DaysOfWeekFragment daysOfWeekFragment = (DaysOfWeekFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (daysOfWeekFragment != null && !daysOfWeekFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(daysOfWeekFragment).commit();
        }
    }
    public void hideMedTimeFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedTimeFragment medTimeFragment = (MedTimeFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medTimeFragment != null && !medTimeFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medTimeFragment).commit();
        }
    }
    public void hideHowTimesDayFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedHowTimesDayFragment medHowTimesDayFragment = (MedHowTimesDayFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medHowTimesDayFragment != null && !medHowTimesDayFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medHowTimesDayFragment).commit();
        }
    }
    public void hideMedFirstDoseFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedFirstDoseTimeFragment medFirstDoseTimeFragment = (MedFirstDoseTimeFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFirstDoseTimeFragment != null && !medFirstDoseTimeFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFirstDoseTimeFragment).commit();
        }
    }
    public void hideMedSecondDoseFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.when_do_you_need_to_take_the_first_dose);
        MedSecondDoseTimeFragment medSecondDoseTimeFragment = (MedSecondDoseTimeFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medSecondDoseTimeFragment != null && !medSecondDoseTimeFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medSecondDoseTimeFragment).commit();
        }
    }
    public void hideEveryXHoursFragment() {
        progressBar.setProgress(40);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        EveryXHoursFragment everyXHoursFragment = (EveryXHoursFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (everyXHoursFragment != null && !everyXHoursFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(everyXHoursFragment).commit();
        }
    }
    public void hideMedEveryOtherDayFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedChooseFirstDayFragment medEveryOtherDayFragment = (MedChooseFirstDayFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medEveryOtherDayFragment != null && !medEveryOtherDayFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medEveryOtherDayFragment).commit();
        }
    }
    public void hideMedEveryXDaysFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedEveryXDaysFragment medEveryXDaysFragment = (MedEveryXDaysFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medEveryXDaysFragment != null && !medEveryXDaysFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medEveryXDaysFragment).commit();
        }
    }
    public void hideMedEveryXWeeksFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedEveryXWeeks medEveryXWeeks = (MedEveryXWeeks) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medEveryXWeeks != null && !medEveryXWeeks.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medEveryXWeeks).commit();
        }
    }
    public void hideMedEveryXMonthsFragment() {
        progressBar.setProgress(30);
        icon.setImageResource(R.drawable.calendar_icon_blue);
        message.setText(R.string.how_often_do_you_take_it);
        MedEveryXMonths medEveryXMonths = (MedEveryXMonths) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medEveryXMonths != null && !medEveryXMonths.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medEveryXMonths).commit();
        }
    }
    public void hideMedReviewRemindersFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.when_do_you_need_to_take_the_first_dose);
        MedReviewRemindersFragment medReviewRemindersFragment = (MedReviewRemindersFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medReviewRemindersFragment != null && !medReviewRemindersFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medReviewRemindersFragment).commit();
        }
    }
    public void hideTimePickerFragment() {
        TimerPickerFragment timerPickerFragment = (TimerPickerFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (timerPickerFragment != null && !timerPickerFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(timerPickerFragment).commit();
        }
    }
    public void hideMedAdditionalInformationFragment() {
        progressBar.setProgress(50);
        icon.setImageResource(R.drawable.alarm_clock_icon);
        message.setText(R.string.when_do_you_need_to_take_the_first_dose);
        MedAdditionalInformationFragment medAdditionalInformationFragment = (MedAdditionalInformationFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medAdditionalInformationFragment != null && !medAdditionalInformationFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medAdditionalInformationFragment).commit();
        }
    }
    public void hideAddMedCountFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.also_add_info);
        message.setText(R.string.what_also_would_you_like_to_add);
        AddMedCountFragment addMedCountFragment = (AddMedCountFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (addMedCountFragment != null && !addMedCountFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(addMedCountFragment).commit();
        }
    }
    public void hideAddMedWithFoodFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.also_add_info);
        message.setText(R.string.what_also_would_you_like_to_add);
        AddMedWithFoodFragment addMedWithFoodFragment = (AddMedWithFoodFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (addMedWithFoodFragment != null && !addMedWithFoodFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(addMedWithFoodFragment).commit();
        }
    }
    public void hideAddMedInstructionFragment() {
        progressBar.setProgress(90);
        icon.setImageResource(R.drawable.also_add_info);
        message.setText(R.string.what_also_would_you_like_to_add);
        MedAddInstructionFragment medAddInstructionFragment = (MedAddInstructionFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medAddInstructionFragment != null && !medAddInstructionFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medAddInstructionFragment).commit();
        }
    }

}