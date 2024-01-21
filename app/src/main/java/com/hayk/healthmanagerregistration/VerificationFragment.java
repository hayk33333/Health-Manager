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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationFragment extends Fragment {
    TextView message, finish, secondMessage, sendVerifyMessage, back;
    private FirebaseUser user;


    public VerificationFragment(FirebaseUser user) {
        this.user = user;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        message = view.findViewById(R.id.verification_fragment_message);
        finish = view.findViewById(R.id.finish);
        back = view.findViewById(R.id.back);
        secondMessage = view.findViewById(R.id.verification_fragment_second_message);
        sendVerifyMessage = view.findViewById(R.id.send_again);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userEmail = bundle.getString("userEmail", "");
            String m = getString(R.string.we_have_sent_a_notification_to_your_email_address) + " " + userEmail;
            message.setText(m);
        }
        finish.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
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


}
