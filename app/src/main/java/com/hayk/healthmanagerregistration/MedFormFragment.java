package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MedFormFragment extends Fragment {
    ImageView back;
    Button pill, injection, solution, drops, powder, other;
    AddMedicationActivity addMedicationActivity;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        pill = view.findViewById(R.id.pill);
        injection = view.findViewById(R.id.injection);
        solution = view.findViewById(R.id.solution);
        drops = view.findViewById(R.id.drops);
        powder = view.findViewById(R.id.powder);
        other = view.findViewById(R.id.other);
        addMedicationActivity = (AddMedicationActivity) requireActivity();

        pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();
            }
        });
        injection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();

            }
        });
        solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();

            }
        });
        drops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();

            }
        });
        powder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();

            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showMedFrequencyFragment();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationActivity addMedicationActivity = (AddMedicationActivity) requireActivity();
                addMedicationActivity.hideMedFormFragment();
            }
        });



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_form, container, false);
    }
}