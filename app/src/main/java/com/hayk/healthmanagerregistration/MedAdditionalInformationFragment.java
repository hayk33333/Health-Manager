package com.hayk.healthmanagerregistration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;


public class MedAdditionalInformationFragment extends Fragment {
    private ImageView back, firstMark,secondMark,thirdMark;
    private AddMedicationActivity addMedicationActivity;
    private RelativeLayout addInstruction, addCount, medWithEating;
    private String documentId;
    private Button save;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        db = FirebaseFirestore.getInstance();
        save = view.findViewById(R.id.save);
        back = view.findViewById(R.id.back);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firstMark = view.findViewById(R.id.first_mark);
        secondMark = view.findViewById(R.id.second_mark);
        thirdMark = view.findViewById(R.id.third_mark);
        addCount = view.findViewById(R.id.add_count);
        addInstruction = view.findViewById(R.id.add_instruction);
        documentId = getArguments().getString("documentId");
        medWithEating = view.findViewById(R.id.med_with_eating);
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMedToDb();
                getActivity().finish();
            }
        });
        addCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedCountFragment();
            }
        });
        addInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedInstructionFragment();

            }
        });
        medWithEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.showAddMedWithFoodFragment();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideMedAdditionalInformationFragment();
            }
        });

    }

    private void saveMedToDb() {
        String userId = firebaseUser.getUid();

        // Получаем ссылку на коллекцию 'users'
        CollectionReference usersCollection = db.collection("users");

        // Создаем ссылку на документ в коллекции 'users-userId'
        DocumentReference documentReference = usersCollection.document(userId);

        // Получаем ссылку на коллекцию 'meds' внутри документа 'users-userId'
        CollectionReference medsCollection = documentReference.collection("meds");

        // Создаем ссылку на новый документ с заданным идентификатором documentId
        DocumentReference newDocumentRef = medsCollection.document(documentId);

        // Устанавливаем данные в новый документ
        newDocumentRef.set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Документ успешно создан в коллекции 'users/meds'! Документ ID: " + documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при создании документа в коллекции 'users/meds': " + e.getMessage());
                    }
                });
    }

    public void setCheckMarks(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.contains("medWithFood")) {
                                thirdMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                thirdMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }
                            if (documentSnapshot.contains("medRemindCount")) {
                                secondMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                secondMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }
                            if (documentSnapshot.contains("medInstruction")) {
                                firstMark.setVisibility(View.VISIBLE);
                                System.out.println("Строка 'medWithFood' существует в документе");
                            } else {
                                firstMark.setVisibility(View.GONE);
                                System.out.println("Строка 'medWithFood' не существует в документе");
                            }

                        } else {
                            firstMark.setVisibility(View.GONE);
                            secondMark.setVisibility(View.GONE);
                            thirdMark.setVisibility(View.GONE);
                            System.out.println("Документ не существует");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.printf("Ошибка при чтении из базы данных");
                    }
                });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_med_additional_information, container, false);
    }
}