package com.hayk.healthmanagerregistration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitNameFragment extends Fragment {
    private EditText visitName;
    private String documentId;
    private Button next;
    private FirebaseFirestore db;
    private ImageView back;
    private Drawable red_et_background;
    private Drawable et_background;
    private TextView message;
    private AddVisitActivity addVisitActivity;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        message = view.findViewById(R.id.message);
        visitName = view.findViewById(R.id.visit_name_et);
        documentId = getArguments().getString("documentId");
        next = view.findViewById(R.id.next);
        db = FirebaseFirestore.getInstance();
        back = view.findViewById(R.id.back);
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        addVisitActivity = (AddVisitActivity) requireActivity();
        visitName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                visitName.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(view1 -> getActivity().finish());
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitName.setBackground(et_background);
                String name = visitName.getText().toString();
                if (name.isEmpty()) {
                    visitName.setBackground(red_et_background);
                    message.setText(R.string.please_enter_the_visit_name);
                    return;
                }
                addVisitNameToDB(name);
                addVisitActivity.showVisitChooseDayFragment();
            }
        });


    }

    private void addVisitNameToDB(String name) {
        CollectionReference visitsCollection = db.collection("visits");
        Map<String, Object> visitData = new HashMap<>();
        visitData.put("visitName", name);
        visitsCollection
                .document(documentId)
                .update(visitData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_name, container, false);
    }
}