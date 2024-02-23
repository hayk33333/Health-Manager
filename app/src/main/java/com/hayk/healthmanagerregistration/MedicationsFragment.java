package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;


public class MedicationsFragment extends Fragment {
    Button addMedButton;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMedButton = view.findViewById(R.id.add_med_button);
        addMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingMedication();
            }
        });

    }

    private void startAddingMedication() {
        Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medications, container, false);
    }
}