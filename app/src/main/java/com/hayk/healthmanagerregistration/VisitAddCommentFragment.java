package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitAddCommentFragment extends Fragment {
    private EditText commentEt;
    private AddVisitActivity addVisitActivity;
    private String documentId;
    private FirebaseFirestore db;
    private ImageView back;
    private Button next;



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentEt = view.findViewById(R.id.visit_comment);
        addVisitActivity = (AddVisitActivity) requireActivity();
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.hideVisitCommentFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEt.getText().toString();
                addVisitCommentToDB(comment);
                addVisitActivity.hideVisitCommentFragment();
            }
        });

    }

    private void addVisitCommentToDB(String comment) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        if (comment.isEmpty()) {
            visitData.put("visitComment", null);
        } else {
            visitData.put("visitComment", comment);
        }

        visitsCollection
                .document(documentId)
                .update(visitData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
        return inflater.inflate(R.layout.fragment_visit_add_comment, container, false);
    }
}