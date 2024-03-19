package com.hayk.healthmanagerregistration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class ForgotPasswordFragment extends Fragment {
    private Button submit_button;
    private TextView back, message;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    Drawable red_et_background, et_background;
    ProgressBar progressBar;
    FrameLayout overlay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progress_circular);
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        submit_button = view.findViewById(R.id.forgot_password_button);
        email = view.findViewById(R.id.forgot_password_email);
        back = view.findViewById(R.id.forgot_password_back);
        message = view.findViewById(R.id.forgot_password_message);
        overlay = view.findViewById(R.id.overlay);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                if (loginActivity != null) {
                    showProgressBar();
                    loginActivity.hideForgotPasswordFragment();
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                email.setBackground(et_background);
                message.setText("");
                if (userEmail.isEmpty()) {
                    email.setBackground(red_et_background);
                    message.setText(R.string.please_enter_your_email_address);
                } else {
                    showProgressBar();
                    isUserWithEmailExists(userEmail, new OnUserExistsListener() {
                        @Override
                        public void onUserExists(boolean exists) {
                            if (exists) {
                                sendPasswordEmail();
                            } else {
                                email.setBackground(red_et_background);
                                message.setText(R.string.user_not_found);
                                hideProgressBar();
                            }
                        }
                    });

                }
            }
        });

    }
    private void isUserWithEmailExists(String email, OnUserExistsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        Query query = usersRef.whereEqualTo("email", email);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exists = !task.getResult().isEmpty();
                    listener.onUserExists(exists);
                } else {
                    hideProgressBar();
                    System.out.println(task.getException().getMessage().toString());

                }
            }
        });
    }


    private void sendPasswordEmail() {
        showProgressBar();
        String userEmail = email.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hideProgressBar();
                        LoginActivity loginActivity = (LoginActivity) getActivity();
                        if (loginActivity != null) {
                            showProgressBar();
                            loginActivity.hideForgotPasswordFragment();
                        }

                    } else {
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage.contains("email address is badly formatted")) {
                            message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                            email.setBackground(red_et_background);
                        }

                        hideProgressBar();
                        System.out.println("Ошибка при отправке email для сброса пароля: " + errorMessage);
                    }
                });


    }
    private void blockFragment(){
        overlay.setVisibility(View.VISIBLE);
        submit_button.setEnabled(false);
        back.setEnabled(false);
        email.setEnabled(false);


    }
    private void unBlockFragment(){
        overlay.setVisibility(View.GONE);
        submit_button.setEnabled(true);
        back.setEnabled(true);
        email.setEnabled(true);

    }
    public void showProgressBar(){
        blockFragment();
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        unBlockFragment();
        progressBar.setVisibility(View.GONE);
    }

}
