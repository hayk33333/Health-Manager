package com.hayk.healthmanagerregistration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VisitInfoActivity extends AppCompatActivity {

    private TextView visitData, visitTime, visitAdditionalInfo, hospitalName, hospitalAddress, hospitalEmail,
            hospitalPhone, hospitalWebSite, hospitalAdditionalInfo, doctorName, doctorSpecialization,
            doctorOfficeLocation, doctorPhone, doctorEmail, doctorAdditionalInfo;
    private FirebaseFirestore db;
    private LinearLayout doctorLayout, hospitalLayout;
    private ImageView back, delete, callHospital, smsHospital, callDoctor, smsDoctor, locationHospital;
    private ProgressBar progressBar;
    private View firestView, secondView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_info);
        locationHospital = findViewById(R.id.location_hospital);
        callDoctor = findViewById(R.id.call_doctor);
        callHospital = findViewById(R.id.call_hospital);
        smsDoctor = findViewById(R.id.sms_doctor);
        smsHospital = findViewById(R.id.sms_hospital);
        back = findViewById(R.id.back);
        firestView = findViewById(R.id.firstView);
        secondView = findViewById(R.id.secondView);
        delete = findViewById(R.id.delete);
        progressBar = findViewById(R.id.progress_circular);
        doctorLayout = findViewById(R.id.doctor_layout);
        hospitalLayout = findViewById(R.id.hospital_layout);
        db = FirebaseFirestore.getInstance();
        visitData = findViewById(R.id.visit_data);
        visitTime = findViewById(R.id.visit_time);
        visitAdditionalInfo = findViewById(R.id.visit_additional_comment);
        hospitalName = findViewById(R.id.hospital_name);
        hospitalAddress = findViewById(R.id.hospital_address);
        hospitalEmail = findViewById(R.id.hospital_email);
        hospitalPhone = findViewById(R.id.hospital_phone);
        hospitalWebSite = findViewById(R.id.hospital_web);
        hospitalAdditionalInfo = findViewById(R.id.hospital_additional_comment);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpecialization = findViewById(R.id.doctor_specialization);
        doctorOfficeLocation = findViewById(R.id.doctor_office_location);
        doctorPhone = findViewById(R.id.doctor_phone);
        doctorEmail = findViewById(R.id.doctor_email);
        doctorAdditionalInfo = findViewById(R.id.doctor_additional_comment);
        String visitId = getIntent().getStringExtra("visitId");
        callDoctor.setVisibility(View.GONE);
        callHospital.setVisibility(View.GONE);
        smsDoctor.setVisibility(View.GONE);
        smsHospital.setVisibility(View.GONE);
        locationHospital.setVisibility(View.GONE);
        hospitalLayout.setVisibility(View.GONE);
        doctorLayout.setVisibility(View.GONE);
        getData(visitId);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteVisit(visitId);
            }
        });

    }

    private void deleteVisit(String visitId) {
        db.collection("visits").document(visitId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userVisits").document(visitId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        finish();
    }

    private void getData(String visitId) {
        CollectionReference medsCollection = db.collection("visits");

        medsCollection.document(visitId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            long day = documentSnapshot.getLong("day");
                            long month = documentSnapshot.getLong("month");
                            long year = documentSnapshot.getLong("year");
                            String time = documentSnapshot.getString("visitTime");
                            String visitComment = documentSnapshot.getString("visitComment");
                            if (visitComment == null) {
                                visitAdditionalInfo.setVisibility(View.GONE);
                            } else {
                                visitAdditionalInfo.setText(visitComment);
                            }
                            setData((int)day, (int)month, (int)year, time);
                            boolean isDoctorDataExist = documentSnapshot.getBoolean("isDoctorDataExist");
                            boolean isHospitalDataExist = documentSnapshot.getBoolean("isHospitalDataExist");
                            if (isDoctorDataExist){
                                doctorLayout.setVisibility(View.VISIBLE);
                                String doctorNameStr = documentSnapshot.getString("doctorName");
                                String doctorEmailStr = documentSnapshot.getString("doctorEmail");
                                String doctorPhoneStr = documentSnapshot.getString("doctorPhone");
                                String doctorSpecializationStr = documentSnapshot.getString("doctorSpecialization");
                                String doctorCommentStr = documentSnapshot.getString("doctorComment");
                                String doctorOfficeLocationStr = documentSnapshot.getString("doctorOfficeLocation");
                                if (doctorNameStr == null){
                                    doctorName.setVisibility(View.GONE);
                                } else {
                                    doctorName.setText(doctorNameStr);
                                }
                                if (doctorEmailStr == null){
                                    doctorEmail.setVisibility(View.GONE);
                                } else {
                                    doctorEmail.setText(getString(R.string.email_) + doctorEmailStr);
                                }
                                if (doctorPhoneStr == null){
                                    doctorPhone.setVisibility(View.GONE);
                                } else {
                                    callDoctor.setVisibility(View.VISIBLE);
                                    smsDoctor.setVisibility(View.VISIBLE);
                                    callDoctor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialPhoneNumber(doctorPhoneStr);
                                        }
                                    });
                                    smsDoctor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sendSMS(doctorPhoneStr);
                                        }
                                    });
                                    doctorPhone.setText(getString(R.string.phone) + doctorPhoneStr);
                                }
                                if (doctorSpecializationStr == null){
                                    doctorSpecialization.setVisibility(View.GONE);
                                } else {
                                    doctorSpecialization.setText(getString(R.string.specialization_) + doctorSpecializationStr);
                                }
                                if (doctorOfficeLocationStr == null){
                                    doctorOfficeLocation.setVisibility(View.GONE);
                                } else {
                                    doctorOfficeLocation.setText(getString(R.string.office_location) + doctorOfficeLocationStr);
                                }
                                if (doctorCommentStr == null){
                                    doctorAdditionalInfo.setVisibility(View.GONE);
                                } else {
                                    doctorAdditionalInfo.setText(doctorCommentStr);
                                }
                            } else {
                                secondView.setVisibility(View.GONE);
                                doctorLayout.setVisibility(View.GONE);
                            }
                            if (isHospitalDataExist){
                                hospitalLayout.setVisibility(View.VISIBLE);
                                String hospitalNameStr = documentSnapshot.getString("hospitalName");
                                String hospitalEmailStr = documentSnapshot.getString("hospitalEmail");
                                String hospitalCommentStr = documentSnapshot.getString("hospitalComment");
                                String hospitalPhoneStr = documentSnapshot.getString("hospitalPhone");
                                String hospitalWebsiteStr = documentSnapshot.getString("hospitalWebsite");
                                String hospitalAddressStr = documentSnapshot.getString("hospitalAddress");
                                if (hospitalNameStr == null){
                                    hospitalName.setVisibility(View.GONE);
                                } else {
                                    hospitalName.setText(hospitalNameStr);
                                }
                                if (hospitalEmailStr == null){
                                    hospitalEmail.setVisibility(View.GONE);
                                } else {
                                    hospitalEmail.setText(getString(R.string.email_) + hospitalEmailStr);
                                }
                                if (hospitalCommentStr == null){
                                    hospitalAdditionalInfo.setVisibility(View.GONE);
                                } else {
                                    hospitalAdditionalInfo.setText(hospitalCommentStr);
                                }
                                if (hospitalPhoneStr == null){

                                    hospitalPhone.setVisibility(View.GONE);
                                } else {
                                    callHospital.setVisibility(View.VISIBLE);
                                    smsHospital.setVisibility(View.VISIBLE);
                                    callHospital.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialPhoneNumber(hospitalPhoneStr);
                                        }
                                    });
                                    smsHospital.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sendSMS(hospitalPhoneStr);
                                        }
                                    });
                                    hospitalPhone.setText(getString(R.string.phone) + hospitalPhoneStr);
                                }
                                if (hospitalWebsiteStr == null){
                                    hospitalWebSite.setVisibility(View.GONE);
                                } else {
                                    hospitalWebSite.setText(getString(R.string.Website) + hospitalWebsiteStr);
                                }
                                if (hospitalAddressStr == null){
                                    hospitalAddress.setVisibility(View.GONE);
                                } else {
                                    locationHospital.setVisibility(View.VISIBLE);
                                    hospitalAddress.setText(getString(R.string.Address) + hospitalAddressStr);
                                    locationHospital.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            double latitude = documentSnapshot.getDouble("latitude");
                                            double longitude = documentSnapshot.getDouble("longitude");
                                            String coordinates = latitude + "," + longitude;

                                            Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + coordinates + "(Место)");

                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);
                                            } else {
                                                Toast.makeText(VisitInfoActivity.this, "Please install google navigator and try again", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });
                                }

                            } else {
                                firestView.setVisibility(View.GONE);

                                hospitalLayout.setVisibility(View.GONE);
                            }


                        } else {

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }

    private void setData(int day, int month, int year, String time) {

        String formattedTime = getString(R.string.time) + formatTime(time);

        String formattedDate = getString(R.string.data) + String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year);

        visitData.setText(formattedDate);
        visitTime.setText(formattedTime);

    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("H:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void sendSMS(String phoneNumber) {
        Uri smsUri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}