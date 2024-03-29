package com.hayk.healthmanagerregistration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerificationFragment extends Fragment {
    TextView message, finish, secondMessage, sendVerifyMessage, back;
    private FirebaseUser user;
    FrameLayout overlay;
    ProgressBar progressBar;
    FirebaseDatabase database;
    Intents intents;


    public VerificationFragment(FirebaseUser user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intents = new Intents(getActivity());
        message = view.findViewById(R.id.verification_fragment_message);
        finish = view.findViewById(R.id.finish);
        back = view.findViewById(R.id.back);
        secondMessage = view.findViewById(R.id.verification_fragment_second_message);
        sendVerifyMessage = view.findViewById(R.id.send_again);
        overlay = view.findViewById(R.id.overlay);
        progressBar = view.findViewById(R.id.progress_circular);
        database = FirebaseDatabase.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userEmail = bundle.getString("userEmail", "");
            String m = getString(R.string.we_have_sent_a_notification_to_your_email_address) + "\n" + userEmail;
            message.setText(m);
        }
        finish.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                showProgressBar();
                secondMessage.setTextColor(Color.BLACK);
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean isEmailVerified = user.isEmailVerified();
                        if (isEmailVerified) {
                            Bundle bundle = getArguments();
                            if (bundle != null) {
                                String userId = bundle.getString("userId");
                                DatabaseReference usersRef = database.getReference("users");
                                DatabaseReference userIdRef = usersRef.child(userId);
                                userIdRef.child("isEmailVerified").setValue(true);
                            }
                            secondMessage.setTextColor(Color.BLACK);
                            intents.MainActivity();
                        } else {
                            hideProgressBar();
                            secondMessage.setTextColor(Color.RED);
                        }
                    }
                });
            }
        });
        sendVerifyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationEmail();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationActivity registrationActivity = (RegistrationActivity) requireActivity();
                registrationActivity.hideVerificationFragment(user);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RegistrationActivity registrationActivity = (RegistrationActivity) requireActivity();
        registrationActivity.deleteUser(user);


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

    private void blockFragment() {
        overlay.setVisibility(View.VISIBLE);
        sendVerifyMessage.setEnabled(false);
        back.setEnabled(false);
        finish.setEnabled(false);


    }

    private void unBlockFragment() {
        overlay.setVisibility(View.GONE);
        sendVerifyMessage.setEnabled(true);
        back.setEnabled(true);
        finish.setEnabled(true);

    }

    public void showProgressBar() {
        blockFragment();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        unBlockFragment();
        progressBar.setVisibility(View.GONE);
    }


}
