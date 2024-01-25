package com.hayk.healthmanagerregistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationActivity extends AppCompatActivity  {
    TextView logIn;
    private Button eyeButton;
    private boolean isEyeButtonOpen = false;
    private EditText password;
    public EditText email;
    private EditText username;
    private Button registerButton;
    private Button googleButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Drawable red_et_background;
    Drawable et_background;
    TextView message;
    private GoogleApiClient googleApiClient;
    NetworkCheckThread networkCheckThread = new NetworkCheckThread(this);
    Intents intents = new Intents(this);





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        GoogleSignInClient client;
//        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        client = GoogleSignIn.getClient(this,options);
        setContentView(R.layout.activity_registration);
        networkCheckThread.startThread();
        networkCheckThread.start();
        logIn = findViewById(R.id.log_in);
        eyeButton = findViewById(R.id.eye_button);
        Drawable open_eye_background = getResources().getDrawable(R.drawable.open_eye_bg);
        Drawable close_eye_background = getResources().getDrawable(R.drawable.close_eye_bg);
        red_et_background = getResources().getDrawable(R.drawable.red_et_background);
        et_background = getResources().getDrawable(R.drawable.edit_text_background);
        password = findViewById(R.id.registration_password);
        email = findViewById(R.id.registration_email);
        username = findViewById(R.id.registration_name);
        googleButton = findViewById(R.id.google_sign_in_button);
        registerButton = findViewById(R.id.registration_button);
        message = findViewById(R.id.registration_message);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intents.LoginActivity();
            }
        });
        //eye button
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
        //registration
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        //EditText text change
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
//        googleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = client.getSignInIntent();
//                startActivityForResult(i,1234);
//            }
//        });


    }



    public void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userName = username.getText().toString().trim();
        username.setBackground(et_background);
        email.setBackground(et_background);
        password.setBackground(et_background);

        if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty()) {

            if (userName.isEmpty()) {
                username.setBackground(red_et_background);
                message.setText(R.string.please_fill_in_all_fields);
            }
            if (userEmail.isEmpty()) {
                email.setBackground(red_et_background);
                message.setText(R.string.please_fill_in_all_fields);

            }
            if (userPassword.isEmpty()) {
                password.setBackground(red_et_background);
                message.setText(R.string.please_fill_in_all_fields);
            }
        } else {
            // Регистрация пользователя в Firebase
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(RegistrationActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Пользователь успешно зарегистрирован
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            message.setText("");
                            addUserToDatabase(user, userName, userEmail);
                            sendVerificationEmail(user);

                        } else {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage.contains("email address is badly formatted")) {
                                // Ошибка: Неправильный формат электронной почты
                                message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                                email.setBackground(red_et_background);
                            } else if (errorMessage.contains("The email address is already in use by another account")) {
                                message.setText(R.string.the_email_address_is_already_in_use_by_another_account);
                                email.setBackground(red_et_background);
                            } else if (errorMessage.contains("Password should be at least 6 characters")) {
                                // Ошибка: Пароль слишком короткий
                                message.setText(R.string.password_should_be_at_least_6_characters);
                                password.setBackground(red_et_background);

                            } else {
                                System.err.println(errorMessage);
                            }
                        }
                    });
        }
    }



    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Электронное письмо с подтверждением успешно отправлено
                        showVerificationFragment(user);

                    } else {
                        // Обработка ошибок отправки электронного письма
                        message.setText(R.string.failed_to_send_confirmation_email);
                    }
                });
    }

    // Добавление пользователя в базу данных
    private void addUserToDatabase(FirebaseUser user, String userName, String email) {
        String userId = user.getUid();
        DatabaseReference userReference = databaseReference.child(userId);
        userReference.child("username").setValue(userName);
        userReference.child("email").setValue(email);
    }

    private void showVerificationFragment(FirebaseUser user) {
        blockActivity();
        VerificationFragment verificationFragment = new VerificationFragment(user);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, verificationFragment)
                .addToBackStack(null)
                .commit();

        Bundle bundle = new Bundle();
        bundle.putString("userEmail", email.getText().toString());


        verificationFragment.setArguments(bundle);
    }

    public void hideVerificationFragment(FirebaseUser user) {
        // Удаляем фрагмент из контейнера
        VerificationFragment verificationFragment = (VerificationFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (verificationFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(verificationFragment).commit();
            unblockActivity();
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!user.isEmailVerified()) {
                        deleteUser();
                    }
                }
            });


        }
    }

    public void deleteUser(){
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {

                    }
                });
    }


    public void blockActivity() {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        username.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        registerButton.setEnabled(false);
        eyeButton.setEnabled(false);
        logIn.setEnabled(false);
        googleButton.setEnabled(false);
    }

    public void unblockActivity() {
        findViewById(R.id.overlay).setVisibility(View.GONE);
        username.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        registerButton.setEnabled(true);
        eyeButton.setEnabled(true);
        logIn.setEnabled(true);
        googleButton.setEnabled(true);
    }
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1234){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
//                FirebaseAuth.getInstance().signInWithCredential(credential)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful()){
//                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
//                                    startActivity(intent);
//
//                                }else {
//                                }
//
//                            }
//                        });
//
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }


}





