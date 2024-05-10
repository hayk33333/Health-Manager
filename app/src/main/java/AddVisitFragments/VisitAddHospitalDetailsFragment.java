package AddVisitFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.MapsActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;


public class VisitAddHospitalDetailsFragment extends Fragment {
    private EditText nameEt, emailEt, phoneNumberEt, addressEt, websiteEt, commentEt;
    private AddVisitActivity addVisitActivity;
    private String documentId;
    private FirebaseFirestore db;
    private ImageView back;
    private Button next, addLocation;
    private TextView addressTV;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressTV = view.findViewById(R.id.address);
        db = FirebaseFirestore.getInstance();
        documentId = getArguments().getString("documentId");
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        nameEt = view.findViewById(R.id.hospital_name);
        emailEt = view.findViewById(R.id.hospital_email);
        phoneNumberEt = view.findViewById(R.id.hospital_phone);
        addLocation = view.findViewById(R.id.add_location);
        //addressEt = view.findViewById(R.id.hospital_address);
        websiteEt = view.findViewById(R.id.hospital_web);
        getAddressFromDB();
        commentEt = view.findViewById(R.id.hospital_additional_comments);
        addVisitActivity = (AddVisitActivity) requireActivity();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitActivity.hideVisitHospitalDetailsFragment();
            }
        });
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hospitalName = nameEt.getText().toString().trim();
                String hospitalEmail = emailEt.getText().toString().trim();
                String hospitalPhone = phoneNumberEt.getText().toString().trim();
                //String hospitalAddress = addressEt.getText().toString().trim();
                String hospitalWebsite = websiteEt.getText().toString().trim();
                String hospitalComment = commentEt.getText().toString().trim();
                addHospitalDetailsToDB(hospitalName, hospitalEmail, hospitalPhone, hospitalWebsite, hospitalComment);
                addVisitActivity.hideVisitHospitalDetailsFragment();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getAddressFromDB();
    }

    private void getAddressFromDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference medsCollection = db.collection("visits");

        medsCollection.document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("hospitalAddress");
                            if (address != null) {
                                addressTV.setText(getString(R.string.address_) + address);

                            }
                        } else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("Ошибка при чтении из базы данных");
                    }
                });
    }


    private void addHospitalDetailsToDB(String name, String email, String phone,
                                        String website, String comment) {
        CollectionReference visitsCollection = db.collection("visits");

        Map<String, Object> visitData = new HashMap<>();
        boolean isDataExist;
        if (name.isEmpty() && email.isEmpty() && phone.isEmpty() && website.isEmpty() && comment.isEmpty()){
            isDataExist = false;
        } else {
            isDataExist = true;
        }

        if (name.isEmpty()) {
            visitData.put("hospitalName", null);
        } else {
            visitData.put("hospitalName", name);
        }
        if (email.isEmpty()) {
            visitData.put("hospitalEmail", null);

        } else {
            visitData.put("hospitalEmail", email);

        }
        if (phone.isEmpty()) {
            visitData.put("hospitalPhone", null);
        } else {
            visitData.put("hospitalPhone", phone);
        }
        if (website.isEmpty()) {
            visitData.put("hospitalWebsite", null);
        } else {
            visitData.put("hospitalWebsite", website);
        }
        if (comment.isEmpty()) {
            visitData.put("hospitalComment", null);
        } else {
            visitData.put("hospitalComment", comment);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_add_hospital_details, container, false);
    }
}