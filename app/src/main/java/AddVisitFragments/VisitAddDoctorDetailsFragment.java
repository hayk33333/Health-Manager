package AddVisitFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;


public class VisitAddDoctorDetailsFragment extends Fragment {
    private EditText nameEt, emailEt, phoneNumberEt, specializationEt, officeLocationEt, commentEt;
    private AddVisitActivity addVisitActivity;
    private String documentId;
    private FirebaseFirestore db;
    private ImageView back;
    private Button next;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameEt = view.findViewById(R.id.doctor_name);
        emailEt = view.findViewById(R.id.doctor_email);
        phoneNumberEt = view.findViewById(R.id.doctor_phone);
        specializationEt = view.findViewById(R.id.doctor_specialization);
        officeLocationEt = view.findViewById(R.id.doctor_office_location);
        commentEt = view.findViewById(R.id.doctor_additional_comments);
        addVisitActivity = (AddVisitActivity) requireActivity();
        documentId = getArguments().getString("documentId");
        db = FirebaseFirestore.getInstance();
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String doctorName = nameEt.getText().toString();
                String doctorEmail = emailEt.getText().toString();
                String doctorPhone = phoneNumberEt.getText().toString();
                String doctorSpecialization = specializationEt.getText().toString();
                String doctorOfficeLocation = officeLocationEt.getText().toString();
                String doctorComment = commentEt.getText().toString();
                addDoctorDetailsToDB(doctorName, doctorEmail, doctorPhone, doctorSpecialization, doctorOfficeLocation, doctorComment);
                addVisitActivity.hideVisitDoctorDetailsFragment();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.hideVisitDoctorDetailsFragment();
            }
        });


    }

    private void addDoctorDetailsToDB(String name, String email, String phone, String specialization,
                                      String officeLocation, String comment) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        if (name.isEmpty()) {
            visitData.put("doctorName", null);
        } else {
            visitData.put("doctorName", name);
        }
        if (email.isEmpty()) {
            visitData.put("doctorEmail", null);

        } else {
            visitData.put("doctorEmail", email);

        }
        if (phone.isEmpty()) {
            visitData.put("doctorPhone", null);
        } else {
            visitData.put("doctorPhone", phone);
        }
        if (specialization.isEmpty()) {
            visitData.put("doctorSpecialization", null);
        } else {
            visitData.put("doctorSpecialization", specialization);
        }
        if (officeLocation.isEmpty()) {
            visitData.put("doctorOfficeLocation", null);
        } else {
            visitData.put("doctorOfficeLocation", officeLocation);
        }
        if (comment.isEmpty()) {
            visitData.put("doctorComment", null);
        } else {
            visitData.put("doctorComment", comment);
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
        return inflater.inflate(R.layout.fragment_visit_add_doctor_details, container, false);
    }
}