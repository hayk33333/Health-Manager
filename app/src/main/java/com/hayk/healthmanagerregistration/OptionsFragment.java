package com.hayk.healthmanagerregistration;

import static android.app.Activity.RESULT_OK;
import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class OptionsFragment extends Fragment {
    private RelativeLayout logOut, changeUsername, changePassword, deleteAccount;
    private FirebaseAuth firebaseAuth;
    private TextView userName;
    private ImageView userImg;
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Bitmap imageBitmap;
    private FirebaseFirestore db;
    private View blockFragment;
    private LinearLayout changeUsernameDialog, changePasswordDialog;
    private TextView usernameOk, userNameCancel, passwordOk, passwordCancel;
    private EditText changeUserNameEt, changePasswordOldEt, changePasswordNewEt, changePasswordConfirmEt;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteAccount = view.findViewById(R.id.delete_account);
        changePasswordDialog = view.findViewById(R.id.change_password_dialog);
        passwordOk = view.findViewById(R.id.ok_password);
        passwordCancel = view.findViewById(R.id.cancel_password);
        changePasswordConfirmEt = view.findViewById(R.id.change_password_confirm_et);
        changePasswordOldEt = view.findViewById(R.id.change_password_old_et);
        changePasswordNewEt = view.findViewById(R.id.change_password_new_et);
        changeUserNameEt = view.findViewById(R.id.change_username_et);
        usernameOk = view.findViewById(R.id.ok_username);
        userNameCancel = view.findViewById(R.id.cancel_username);
        db = FirebaseFirestore.getInstance();
        changeUsernameDialog = view.findViewById(R.id.change_username_dialog);
        changePassword = view.findViewById(R.id.change_password);
        logOut = view.findViewById(R.id.log_out);
        changeUsername = view.findViewById(R.id.change_username);
        firebaseAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.userName);
        userName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        userImg = view.findViewById(R.id.userImg);
        blockFragment = view.findViewById(R.id.blockView);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
        blockFragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeName();
            }
        });
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.select_image_source);
                builder.setItems(new CharSequence[]{getString(R.string.gallery), getString(R.string.camera)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, PICK_IMAGE);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });


        if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(firebaseAuth.getCurrentUser().getPhotoUrl())
                    .into(userImg);
        } else {
            Glide.with(this)
                    .load(R.drawable.person_icon)
                    .into(userImg);
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(FirebaseAuth.getInstance().getCurrentUser());

            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void addUserImg(Bitmap imageBitmap) {
        Uri photoUri = getImageUri(requireContext(), imageBitmap);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Фотография профиля успешно обновлена.");
                            Glide.with(getActivity())
                                    .load(firebaseAuth.getCurrentUser().getPhotoUrl())
                                    .into(userImg);
                        }
                    }
                });
    }

    private Uri getImageUri(Context context, Bitmap imageBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "ProfileImage", null);
        return Uri.parse(path);
    }

    private void hideKeyboard() {
        // Получение менеджера ввода методов
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

        // Получение текущего фокуса и скрытие клавиатуры
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                            addUserImg(imageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                        addUserImg(imageBitmap);
                    }
                    break;
            }
        }
    }

    private void changeName() {
        changeUserNameEt.setText("");
        blockFragment.setVisibility(View.VISIBLE);
        changeUsernameDialog.setVisibility(View.VISIBLE);
        usernameOk.setEnabled(false);
        usernameOk.setTextColor(getResources().getColor(R.color.hint_color));

        changeUserNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (changeUserNameEt.getText().toString().trim().isEmpty()) {
                    usernameOk.setEnabled(false);
                    usernameOk.setTextColor(getResources().getColor(R.color.hint_color));

                } else {
                    usernameOk.setEnabled(true);
                    usernameOk.setTextColor(getResources().getColor(R.color.my_color));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        userNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                blockFragment.setVisibility(View.GONE);
                changeUsernameDialog.setVisibility(View.GONE);
            }
        });
        usernameOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String newName = changeUserNameEt.getText().toString().trim();
                blockFragment.setVisibility(View.GONE);
                changeUsernameDialog.setVisibility(View.GONE);
                if (!newName.isEmpty()) {
                    updateDisplayName(newName);
                }
            }
        });
    }


    private void updateDisplayName(String newName) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userName.setText(newName);
                            Toast.makeText(requireContext(), R.string.username_successfully_changed, Toast.LENGTH_SHORT).show();
                            hideKeyboard();
                        } else {
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        String userId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference usersCollection = db.collection("users");

        DocumentReference documentReference = usersCollection.document(userId);


        documentReference.update("userName", newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                    }
                });

    }

    private void changePassword() {
        changePasswordOldEt.setText("");
        changePasswordConfirmEt.setText("");
        changePasswordNewEt.setText("");
        blockFragment.setVisibility(View.VISIBLE);
        changePasswordDialog.setVisibility(View.VISIBLE);
        passwordOk.setEnabled(false);
        passwordOk.setTextColor(getResources().getColor(R.color.hint_color));
        changePasswordOldEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (changePasswordConfirmEt.getText().toString().trim().isEmpty() ||
                        changePasswordOldEt.getText().toString().trim().isEmpty() ||
                        changePasswordNewEt.getText().toString().trim().isEmpty()) {
                    passwordOk.setEnabled(false);
                    passwordOk.setTextColor(getResources().getColor(R.color.hint_color));

                } else {
                    passwordOk.setEnabled(true);
                    passwordOk.setTextColor(getResources().getColor(R.color.my_color));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        changePasswordConfirmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (changePasswordConfirmEt.getText().toString().trim().isEmpty() ||
                        changePasswordOldEt.getText().toString().trim().isEmpty() ||
                        changePasswordNewEt.getText().toString().trim().isEmpty()) {
                    passwordOk.setEnabled(false);
                    passwordOk.setTextColor(getResources().getColor(R.color.hint_color));

                } else {
                    passwordOk.setEnabled(true);
                    passwordOk.setTextColor(getResources().getColor(R.color.my_color));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        changePasswordNewEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (changePasswordConfirmEt.getText().toString().trim().isEmpty() ||
                        changePasswordOldEt.getText().toString().trim().isEmpty() ||
                        changePasswordNewEt.getText().toString().trim().isEmpty()) {
                    passwordOk.setEnabled(false);
                    passwordOk.setTextColor(getResources().getColor(R.color.hint_color));

                } else {
                    passwordOk.setEnabled(true);
                    passwordOk.setTextColor(getResources().getColor(R.color.my_color));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                blockFragment.setVisibility(View.GONE);
                changePasswordDialog.setVisibility(View.GONE);
            }
        });
        passwordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String oldPassword = changePasswordOldEt.getText().toString().trim();
                String confirmPassword = changePasswordConfirmEt.getText().toString().trim();
                String newPassword = changePasswordNewEt.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), R.string.passwords_do_not_much, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length() < 6){
                    Toast.makeText(getActivity(), R.string.password_should_be_at_least_6_characters, Toast.LENGTH_SHORT).show();
                    return;
                }

                user.reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(requireContext(), R.string.password_successfully_changed, Toast.LENGTH_SHORT).show();
                                                blockFragment.setVisibility(View.GONE);
                                                changePasswordDialog.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                System.out.println("Failed to change password. " + e.getMessage());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Authentication failed. " + e.getMessage());
                                String errorMessage = e.getMessage();
                                if (errorMessage.contains("The supplied auth credential is incorrect, malformed or has expired")) {
                                    Toast.makeText(requireContext(), "Old password entered incorrectly. Please try again.", Toast.LENGTH_SHORT).show();
                                } else if (errorMessage.contains("A network error")) {
                                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }
    public void deleteUser(FirebaseUser user) {
        deleteUserFromDB(user);
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();

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
                    public void onFailure(@androidx.annotation.NonNull Exception e) {

                        System.out.println("Ошибка при удалении пользователя из Firestore");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, container, false);
    }
}