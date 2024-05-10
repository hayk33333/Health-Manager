package AddVisitFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.AlarmReceiverVisit;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitAdditionalInfoFragment extends Fragment {
    private ImageView back;
    private AddVisitActivity addVisitActivity;
    private RelativeLayout doctorDetails, hospitalDetails, comments;
    private FirebaseFirestore db;
    private String documentId;
    private FirebaseUser currentUser;
    private Button save;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        save = view.findViewById(R.id.save);
        documentId = getArguments().getString("documentId");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        back = view.findViewById(R.id.back);
        doctorDetails = view.findViewById(R.id.doctor_details);
        hospitalDetails = view.findViewById(R.id.hospital_details);
        comments = view.findViewById(R.id.comments);
        setFalse();
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
        comments.setOnClickListener(view13 -> addVisitActivity.showVisitAddCommentFragment());
        save.setOnClickListener(view12 -> {
            getDataForVisitAlarm();
            saveVisitToDb();
            getActivity().finish();
        });

        addVisitActivity = (AddVisitActivity) requireActivity();
        back.setOnClickListener(view1 -> addVisitActivity.hideVisitAdditionalInfoFragment());

    }

    private void setFalse() {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        boolean isDataExist = false;

        visitData.put("isDoctorDataExist", isDataExist);
        visitData.put("isHospitalDataExist", isDataExist);
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

    private void getDataForVisitAlarm() {
        CollectionReference medsCollection = db.collection("visits");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            System.out.println("getData");
                            String visitTime = documentSnapshot.getString("visitTime");
                            String day = String.valueOf(documentSnapshot.getLong("day"));
                            String year = String.valueOf(documentSnapshot.getLong("year"));
                            String month = String.valueOf(documentSnapshot.getLong("month"));
                            String dataType = documentSnapshot.getString("notificationDataType");
                            String dataCount = documentSnapshot.getString("notificationDataCount");
                            setAlarmForVisit(getActivity(), dataType, dataCount, visitTime, year, month, day);
                        } else {
                            System.out.println("med does not exists");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("error to read from db");
                    }
                });
    }

    private void setAlarmForVisit(Context context, String type, String count, String time, String year, String month, String day) {
        System.out.println("setAlarmForVisits()");
        AlarmReceiverVisit alarmReceiverVisit = new AlarmReceiverVisit();
        alarmReceiverVisit.setAlarm(context, type, count, time, year, month, day, documentId);
    }


    private void saveVisitToDb() {
        String userId = currentUser.getUid();

        CollectionReference usersCollection = db.collection("users");

        DocumentReference documentReference = usersCollection.document(userId);

        CollectionReference medsCollection = documentReference.collection("userVisits");

        DocumentReference newDocumentRef = medsCollection.document(documentId);

        newDocumentRef.set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Документ успешно создан в коллекции 'users/visit'! Документ ID: " + documentId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при создании документа в коллекции 'users/visit': " + e.getMessage());
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_addinitional_info, container, false);
    }
}