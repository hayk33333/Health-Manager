package com.hayk.healthmanagerregistration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public class MedEveryOtherDayFragment extends Fragment {
    NumberPicker day, month, year;
    ImageView back;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        day = view.findViewById(R.id.day);
        month = view.findViewById(R.id.month);
        year = view.findViewById(R.id.year);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        year.setMinValue(getYear());
        year.setMaxValue(2100);
        year.setWrapSelectorWheel(false);
        back = view.findViewById(R.id.back);
        year.setValue(getYear());
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setValue(getMonthValue());
        month.setDisplayedValues(months);
        day.setMinValue(1);
        setDay();
        day.setValue(getDay());
        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Вызываем метод, чтобы обработать изменение значения
                setDay();
            }
        });
        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Вызываем метод, чтобы обработать изменение значения
                setDay();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedEveryOtherDayFragment();
            }
        });

    }

    private void setDay() {
        int selectedMonth = month.getValue();
        int selectedYear = year.getValue();
        int maxDay;

        switch (selectedMonth) {
            case 2:
                maxDay = YearMonth.of(selectedYear, Month.FEBRUARY).lengthOfMonth();
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                maxDay = 30;
                break;
            default:
                maxDay = 31;
                break;
        }

        day.setMaxValue(maxDay);
    }


    private int getYear() {
        return LocalDate.now().getYear();
    }

    private int getMonthValue() {
        return LocalDate.now().getMonthValue();
    }
    private int getDay() {
        return LocalDate.now().getDayOfMonth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_med_every_other_day, container, false);
    }
}