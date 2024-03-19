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
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import NoNetwork.NetworkCheckThread;

public class LoginActivity extends AppCompatActivity {
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
    NetworkCheckThread networkCheckThread = new NetworkCheckThread(this);
    Intents intents = new Intents(this);
    ProgressBar progressBar;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    private GoogleSignInClient mGoogleSignInClient;
    Button googleSignInButton;
    FirebaseFirestore db;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        networkCheckThread.startThread();
        networkCheckThread.start();
        registerHere = findViewById(R.id.register_here);
        eyeButton = findViewById(R.id.eye_button);
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
        progressBar = findViewById(R.id.progress_circular);
        googleSignInButton = findViewById(R.id.google_login_button);
        db = FirebaseFirestore.getInstance();


        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                signIn();
            }
        });

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
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
                showProgressBar();
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
            showProgressBar();
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {

                                hideProgressBar();
                                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (user.isEmailVerified()) {
                                            updateUserEmailVerification(user, user.isEmailVerified());
                                        }
                                    }
                                });
                                intents.MainActivity();

                            } else {
                                showVerificationFragment(user);
                            }
                        } else {
                            hideProgressBar();
                            String errorMessage = task.getException().getMessage();

                            if (errorMessage.contains("email address is badly formatted")) {
                                message.setText(R.string.invalid_email_format_please_check_the_entered_address);
                                email.setBackground(red_et_background);
                            } else if (errorMessage.contains("The supplied auth credential is incorrect, malformed or has expired.")) {
                                message.setText(R.string.incorrect_email_or_password);
                            } else {
                                System.out.println(errorMessage);
                            }
                        }
                    });
        }
    }

    private void showForgotPasswordFragment() {
        hideProgressBar();
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
        hideProgressBar();
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
        googleSignInButton.setEnabled(false);
        loginButton.setEnabled(false);
        forgotPassword.setEnabled(false);
    }

    public void unblockActivity() {
        findViewById(R.id.overlay).setVisibility(View.GONE);
        email.setEnabled(true);
        password.setEnabled(true);
        eyeButton.setEnabled(true);
        registerHere.setEnabled(true);
        googleSignInButton.setEnabled(true);
        loginButton.setEnabled(true);
        forgotPassword.setEnabled(true);

    }

    public void showProgressBar() {
        blockActivity();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
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
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed with status code: " + e.getStatusCode(), e);
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
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userName = user.getDisplayName();
                            String password = user.getEmail();
                            addUserToDB(user, userName, password);
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
    private void updateUserEmailVerification(FirebaseUser user, boolean newEmailVerifiedStatus) {
        DocumentReference userRef = db.collection("users").document(user.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("emailVerified", newEmailVerifiedStatus);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Статус верификации почты успешно обновлен");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Ошибка при обновлении статуса верификации почты: " + e.getMessage());
                    }
                });
    }
}