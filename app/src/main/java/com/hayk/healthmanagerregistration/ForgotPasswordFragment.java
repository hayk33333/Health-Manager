package com.hayk.healthmanagerregistration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicBoolean;

public class ForgotPasswordFragment extends Fragment {
    private Button submit_button;
    private TextView back, message;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    Drawable red_et_background, et_background;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        submit_button = view.findViewById(R.id.forgot_password_button);
        email = view.findViewById(R.id.forgot_password_email);
        back = view.findViewById(R.id.forgot_password_back);
        message = view.findViewById(R.id.forgot_password_message);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                if (loginActivity != null) {
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
                    if (isUserWithEmailExists(userEmail)){
                        sendPasswordEmail();
                    }else {
                        email.setBackground(red_et_background);
                        message.setText(R.string.user_not_found);
                    }

                }
            }
        });
    }

    private void sendPasswordEmail() {
        String userEmail = email.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                        // Возникла ошибка при отправке email\
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage.contains("email address is badly formatted")){
                            message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                            email.setBackground(red_et_background);
                        }

                        System.err.println("Ошибка при отправке email для сброса пароля: " + errorMessage);
                    }
                });
    }
    public static boolean isUserWithEmailExists(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        AtomicBoolean isUserExists = new AtomicBoolean(false);

        if (user != null) {
            // Пользователь уже вошел в систему
            return true;
        } else {
            // Пользователь не вошел в систему, проверяем наличие пользователя с указанным email в базе данных Firebase
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Методы входа для указанного email
                            if (task.getResult().getSignInMethods().size() > 0) {
                                // Пользователь с указанным email существует
                                // Вы можете добавить здесь дополнительную логику, если необходимо
                                // например, вывести сообщение или выполнить дополнительные действия
                                // в случае, если пользователь существует
                                isUserExists.set(true);
                            } else {
                                // Пользователь с указанным email не существует
                                // Вы также можете добавить дополнительную логику здесь
                                isUserExists.set(false);
                            }
                        } else {
                            // Ошибка при выполнении запроса
                            // Здесь вы можете обработать ошибку или вывести сообщение об ошибке
                            isUserExists.set(false);
                        }
                    });
        }

        return isUserExists.get();
    }

}
