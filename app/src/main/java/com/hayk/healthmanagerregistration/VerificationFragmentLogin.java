package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class VerificationFragmentLogin extends Fragment {
    Button loginButton;
    TextView registerAgain, sendAgain, back, secondMessage, message;
    private FirebaseUser user;
    FrameLayout overlay;
    ProgressBar progressBar;


    public VerificationFragmentLogin(FirebaseUser user) {
        this.user = user;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton = view.findViewById(R.id.verification_fragment_login_button);
        registerAgain = view.findViewById(R.id.register_again);
        sendAgain = view.findViewById(R.id.send_again);
        back = view.findViewById(R.id.back_login);
        message = view.findViewById(R.id.login_verification_fragment_message);
        secondMessage = view.findViewById(R.id.verification_fragment_login_second_message);
        progressBar = view.findViewById(R.id.progress_circular);
        overlay = view.findViewById(R.id.overlay);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userEmail = bundle.getString("userEmail", "");
            String m = getString(R.string.we_have_sent_a_notification_to_your_email_address) + "\n" + userEmail;
            message.setText(m);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                if (loginActivity != null) {
                    loginActivity.hideVerificationFragment();
                }
            }
        });
        sendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationEmail();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                showProgressBar();
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean isEmailVerified = user.isEmailVerified();
                        if (isEmailVerified) {
                            // Открыть новую активность
                            secondMessage.setTextColor(Color.BLACK);
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // Показать сообщение или выполнить другие действия, если подтверждение не успешно
                            hideProgressBar();
                            secondMessage.setTextColor(Color.RED);
                        }
                    }
                });
            }
        });
        registerAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                deleteUser();
                Intent registrationActivity = new Intent(getContext(), RegistrationActivity.class);
                startActivity(registrationActivity);
            }
        });

    }
    private void sendVerificationEmail() {
        secondMessage.setTextColor(Color.BLACK);
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(taskM -> {
                                if (taskM.isSuccessful()) {
                                    // Электронное письмо с подтверждением успешно отправлено

                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendVerificationEmail();
                                        }
                                    }, 10000);
                                }
                            });
                }
            }
        });


    }
    protected void deleteUser(){
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {

                    }
                });
    }
    private void blockFragment(){
        overlay.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        back.setEnabled(false);
        registerAgain.setEnabled(false);
        sendAgain.setEnabled(false);


    }
    private void unBlockFragment(){
        overlay.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        back.setEnabled(true);
        registerAgain.setEnabled(true);
        sendAgain.setEnabled(true);

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
