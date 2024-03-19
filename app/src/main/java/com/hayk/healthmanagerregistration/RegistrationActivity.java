package com.hayk.healthmanagerregistration;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import NoNetwork.NetworkCheckThread;


public class RegistrationActivity extends AppCompatActivity  {
    TextView logIn;
    private Button eyeButton;
    private boolean isEyeButtonOpen = false;
    private EditText password;
    public EditText email;
    private EditText username;
    private Button registerButton;
    private Button googleSignInButton;
    private EditText repeatPassword;

    private FirebaseAuth firebaseAuth;
    Drawable red_et_background;
    Drawable et_background;
    TextView message;
    NetworkCheckThread networkCheckThread = new NetworkCheckThread(this);
    Intents intents = new Intents(this);
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    private GoogleSignInClient mGoogleSignInClient;
    ProgressBar progressBar;
    private FirebaseFirestore db;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
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
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        registerButton = findViewById(R.id.registration_button);
        repeatPassword = findViewById(R.id.repeat_registration_password);
        message = findViewById(R.id.registration_message);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        db = FirebaseFirestore.getInstance();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                signIn();
            }
        });





        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                intents.LoginActivity();
            }
        });
        eyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEyeButtonOpen) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    repeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeButton.setBackground(close_eye_background);
                    isEyeButtonOpen = false;
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    repeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeButton.setBackground(open_eye_background);
                    isEyeButtonOpen = true;
                }
                password.setSelection(password.getText().length());
                repeatPassword.setSelection(repeatPassword.getText().length());

            }

        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
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

        repeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repeatPassword.setBackground(et_background);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    public void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userName = username.getText().toString().trim();
        String userRepeatPassword = repeatPassword.getText().toString().trim();


        username.setBackground(et_background);
        email.setBackground(et_background);
        password.setBackground(et_background);
        repeatPassword.setBackground(et_background);
        message.setText("");


        if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty() || userRepeatPassword.isEmpty()) {
            message.setText(R.string.please_fill_in_all_fields);
            if (userName.isEmpty()) {
                username.setBackground(red_et_background);
            }
            if (userEmail.isEmpty()) {
                email.setBackground(red_et_background);

            }
            if (userPassword.isEmpty()) {
                password.setBackground(red_et_background);
            }
            if (userRepeatPassword.isEmpty()) {
                repeatPassword.setBackground(red_et_background);
            }


        }
        else {
            showProgressBar();
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(RegistrationActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (userPassword.equals(userRepeatPassword)){
                                message.setText("");
                                addUserToDB(user, userName, userEmail);
                                sendVerificationEmail(user);
                            }else {
                                repeatPassword.setBackground(red_et_background);
                                message.setText(R.string.the_passwords_don_t_match);
                                hideProgressBar();
                                deleteUser(user);
                            }


                        } else {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage.contains("email address is badly formatted")) {
                                message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                                email.setBackground(red_et_background);
                            }if (errorMessage.contains("The email address is already in use by another account")) {
                                message.setText(R.string.the_email_address_is_already_in_use_by_another_account);
                                email.setBackground(red_et_background);
                            }if (errorMessage.contains("Password should be at least 6 characters")) {
                                message.setText(R.string.password_should_be_at_least_6_characters);
                                password.setBackground(red_et_background);
                            }
                            else {
                                System.err.println(errorMessage);
                            }
                            hideProgressBar();
                        }
                    });
        }
    }



    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showVerificationFragment(user);

                    } else {
                        message.setText(R.string.failed_to_send_confirmation_email);
                    }
                });
    }

    private void addUserToDB(FirebaseUser user, String userName, String email) {
        User userParams = new User(userName, email,user.isEmailVerified());
        db.collection("users").document(user.getUid())
                .set(userParams)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Пользователь успешно добавлен с ID: " + user.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
                    }
                });
    }
    private void deleteUserFromDB(FirebaseUser user) {
        String userID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("users").document(userID);

        userRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        System.out.println("Пользователь успешно удален из Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        System.out.println("Ошибка при удалении пользователя из Firestore");
                    }
                });
    }

    private void showVerificationFragment(FirebaseUser user) {
        VerificationFragment verificationFragment = new VerificationFragment(user);
        hideProgressBar();
        blockActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, verificationFragment)
                .addToBackStack(null)
                .commit();

        Bundle bundle = new Bundle();
        bundle.putString("userEmail", email.getText().toString());
        bundle.putString("userId", user.getUid());


        verificationFragment.setArguments(bundle);
    }

    public void hideVerificationFragment(FirebaseUser user) {
        VerificationFragment verificationFragment = (VerificationFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (verificationFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(verificationFragment).commit();
            unblockActivity();
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!user.isEmailVerified()) {
                        deleteUser(user);
                    }
                }
            });


        }
    }

    public void deleteUser(FirebaseUser user){
        deleteUserFromDB(user);
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
        googleSignInButton.setEnabled(false);
        repeatPassword.setEnabled(false);
    }

    public void unblockActivity() {
        findViewById(R.id.overlay).setVisibility(View.GONE);
        username.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        registerButton.setEnabled(true);
        eyeButton.setEnabled(true);
        logIn.setEnabled(true);
        googleSignInButton.setEnabled(true);
        repeatPassword.setEnabled(true);
    }
    public void showProgressBar(){
        blockActivity();
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        unblockActivity();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideProgressBar();


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                System.err.println(e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userName = user.getDisplayName();
                            String email = user.getEmail();

                            addUserToDB(user, userName, email);
                            intents.MainActivity();
                        } else {
                            System.out.println("signInWithCredential:failure" + task.getException());
                        }
                    }
                });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}






