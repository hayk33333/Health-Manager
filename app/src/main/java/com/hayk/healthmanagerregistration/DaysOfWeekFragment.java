package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DaysOfWeekFragment extends Fragment {
    ImageView back;
    Button monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    boolean[] clickable = {false, false, false, false, false, false, false};

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);
        Button[] buttons = {monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        for (int i = 0; i < buttons.length; i++) {
            int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    if (!clickable[j]) {
                        buttons[j].setBackground(getResources().getDrawable(R.drawable.list_selected_button_bg));
                        clickable[j] = true;
                    } else {
                        buttons[j].setBackground(getResources().getDrawable(R.drawable.list_button_bg));
                        clickable[j] = false;
                    }
                }
            });
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideDaysOfWeekFragment();
            }
        });
    }
//        monday.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void onClick(View view) {
//                if (!clickable[0]) {
//                    monday.setBackground(R.color.my_color);
//                    clickable[0] = true;
//                }else {
//                    monday.setBackground(R.color.white);
//                    clickable[0] = false;
//                }
//            }
//        });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_days_of_week, container, false);
    }
}