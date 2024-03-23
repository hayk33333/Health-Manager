package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.checkerframework.checker.nullness.qual.NonNull;


public class VisitAdditionalInfoFragment extends Fragment {
    private ImageView back;
    private AddVisitActivity addVisitActivity;
    RelativeLayout doctorDetails, hospitalDetails, comments;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        doctorDetails = view.findViewById(R.id.doctor_details);
        hospitalDetails = view.findViewById(R.id.hospital_details);
        comments = view.findViewById(R.id.comments);
        doctorDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.showVisitAddDoctorDetailsFragment();
            }
        });
        hospitalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.showVisitAddHospitalDetailsFragment();

            }
        });
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.showVisitAddCommentFragment();

            }
        });

        addVisitActivity = (AddVisitActivity) requireActivity();
        back.setOnClickListener(view1 -> addVisitActivity.hideVisitAdditionalInfoFragment());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_addinitional_info, container, false);
    }
}