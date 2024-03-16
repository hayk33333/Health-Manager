package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TimerPickerFragment extends Fragment {
    NumberPicker hour, minute;
    TextView ok;



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "ayo", Toast.LENGTH_SHORT).show();
        ok = view.findViewById(R.id.ok);
        hour = view.findViewById(R.id.hour);
        hour.setMinValue(0);
        hour.setMaxValue(23);
        minute = view.findViewById(R.id.minute);
        minute.setMinValue(0);
        minute.setMaxValue(11);
        String[] displayedValues = new String[12];
        for (int i = 0; i <= 11; i++) {
            if (i == 0) {
                displayedValues[i] = "00";
            } else {
                displayedValues[i] = String.valueOf(i * 5);
            }
        }
        hour.setValue(8);
        minute.setDisplayedValues(displayedValues);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourValue = hour.getValue();
                int minuteValue = minute.getValue() * 5;
                ArrayList<String> times = getArguments().getStringArrayList("times");
                int position = getArguments().getInt("position");
                String time = String.valueOf(hourValue) + ":" + String.valueOf(minuteValue);
                Bundle bundle = new Bundle();
                bundle.putString("time", time);
                bundle.putInt("position", position);
                bundle.putStringArrayList("times", times);
                MedReviewRemindersFragment medReviewRemindersFragment = new MedReviewRemindersFragment();
                medReviewRemindersFragment.setArguments(bundle);

                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideTimePickerFragment();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer_picker, container, false);
    }
}