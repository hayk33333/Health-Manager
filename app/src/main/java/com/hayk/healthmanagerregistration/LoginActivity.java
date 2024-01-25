package com.hayk.healthmanagerregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity{
    TextView registerHere;
    private Button eyeButton;
    private boolean isEyeButtonOpen = false;
    private EditText password;
    private EditText email;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Drawable red_et_background;
    Drawable et_background;
    TextView message;
    TextView forgotPassword;
    Button googleButton;
    NetworkCheckThread networkCheckThread = new NetworkCheckThread(this);
    Intents intents = new Intents(this);



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        networkCheckThread.startThread();
        networkCheckThread.start();
        registerHere = findViewById(R.id.register_here);
        eyeButton = findViewById(R.id.eye_button);
        googleButton = findViewById(R.id.google_login_button);
        email = findViewById(R.id.login_email);
        loginButton = findViewById(R.id.login_button);
        password = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        message = findViewById(R.id.login_message);
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        Drawable open_eye_background = getResources().getDrawable(R.drawable.open_eye_bg);
        Drawable close_eye_background = getResources().getDrawable(R.drawable.close_eye_bg);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intents.RegistrationActivity();
            }
        });

        eyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEyeButtonOpen) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeButton.setBackground(close_eye_background);
                    isEyeButtonOpen = false;
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeButton.setBackground(open_eye_background);
                    isEyeButtonOpen = true;
                }
                password.setSelection(password.getText().length());

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordFragment();

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
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }





    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        email.setBackground(et_background);
        password.setBackground(et_background);
        message.setText("");

        if (userEmail.isEmpty() || userPassword.isEmpty()) {

            if (userEmail.isEmpty()) {
                email.setBackground(red_et_background);
                message.setText(R.string.please_fill_in_all_fields);
            }
            if (userPassword.isEmpty()) {
                password.setBackground(red_et_background);
                message.setText(R.string.please_fill_in_all_fields);
            }
        } else {
            // Вход пользователя в Firebase
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Пользователь успешно вошел в систему
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            // Проверка наличия верификации
                            if (user != null && user.isEmailVerified()) {
                                // Пользователь верифицирован, выполните действия после входа
                                // Например, переход на другую активность
                                Toast.makeText(this, "Вход успешен!", Toast.LENGTH_SHORT).show();
                                // Intent yourIntent = new Intent(CurrentActivity.this, TargetActivity.class);
                                // startActivity(yourIntent);
                            } else {
                                showVerificationFragment(user);
                            }
                        } else {
                            String errorMessage = task.getException().getMessage();
                            //System.out.println(errorMessage);
                            if (errorMessage.contains("email address is badly formatted")) {
                                // Ошибка: Неправильный формат электронной почты
                                message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                                email.setBackground(red_et_background);
                            } else if (errorMessage.contains("The supplied auth credential is incorrect, malformed or has expired.")) {
                                // Ошибка: Нет пользователя с таким email
                                message.setText(R.string.incorrect_email_or_password);
                            } else {
                                // Общая ошибка
                                System.out.println(errorMessage);
                            }
                        }
                    });
        }
    }

    private void showForgotPasswordFragment() {
        blockActivity();
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, forgotPasswordFragment)
                .addToBackStack(null)
                .commit();
    }

    public void hideForgotPasswordFragment() {
        unblockActivity();
        ForgotPasswordFragment forgotPasswordFragment = (ForgotPasswordFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (forgotPasswordFragment != null && !forgotPasswordFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(forgotPasswordFragment).commit();
        }
    }
    private void showVerificationFragment(FirebaseUser user) {
        blockActivity();
        VerificationFragmentLogin verificationFragmentLogin = new VerificationFragmentLogin(user);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, verificationFragmentLogin)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString("userEmail", email.getText().toString());


        verificationFragmentLogin.setArguments(bundle);
    }
    public void hideVerificationFragment() {
        unblockActivity();
        VerificationFragmentLogin verificationFragmentLogin = (VerificationFragmentLogin) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (verificationFragmentLogin != null && !verificationFragmentLogin.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(verificationFragmentLogin).commit();
        }
    }

    public void blockActivity() {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        eyeButton.setEnabled(false);
        registerHere.setEnabled(false);
        googleButton.setEnabled(false);
        loginButton.setEnabled(false);
        forgotPassword.setEnabled(false);
    }

    public void unblockActivity() {
        findViewById(R.id.overlay).setVisibility(View.GONE);
        email.setEnabled(true);
        password.setEnabled(true);
        eyeButton.setEnabled(true);
        registerHere.setEnabled(true);
        googleButton.setEnabled(true);
        loginButton.setEnabled(true);
        forgotPassword.setEnabled(true);

    }
}