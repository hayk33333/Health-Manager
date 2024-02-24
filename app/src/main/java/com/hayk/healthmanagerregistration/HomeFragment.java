package com.hayk.healthmanagerregistration;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.Calendar;


public class HomeFragment extends Fragment {



    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(4);  // minimum value
        numberPicker.setMaxValue(9);  // maximum value
        numberPicker.setValue(6);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });
    }
    private int mHour, mMinute, mSecond;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    private void showTimePickerDialog() {
        // Получить текущее время
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);

        // Создать диалог выбора времени
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        // Вы можете использовать mHour, mMinute и mSecond здесь
                    }
                }, mHour, mMinute, true); // true для 24-часового формата

        // Показать диалог выбора времени
        timePickerDialog.show();
    }
}