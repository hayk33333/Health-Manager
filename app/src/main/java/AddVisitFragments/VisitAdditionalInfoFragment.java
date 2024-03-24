package AddVisitFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hayk.healthmanagerregistration.AddVisitActivity;
import com.hayk.healthmanagerregistration.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;


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
            saveVisitToDb();
            getActivity().finish();
        });

        addVisitActivity = (AddVisitActivity) requireActivity();
        back.setOnClickListener(view1 -> addVisitActivity.hideVisitAdditionalInfoFragment());

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_addinitional_info, container, false);
    }
}