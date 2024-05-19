package AddMedFragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hayk.healthmanagerregistration.AddMedicationActivity;
import com.hayk.healthmanagerregistration.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MedAddInstructionFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Button addImg, next;
    private LinearLayout imageContainer;
    private Drawable deleteIcon;
    private Bitmap imageBitmap;
    private ArrayList<Bitmap> images;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String documentId;
    private ArrayList<String> imageUrls;
    private AddMedicationActivity addMedicationActivity;
    private ProgressBar progressBar;
    private ImageView back;
    private EditText instructionText;


    @Override

    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new ArrayList<>();
        back = view.findViewById(R.id.back);
        imageUrls = new ArrayList<>();
        addImg = view.findViewById(R.id.add_img);
        imageContainer = view.findViewById(R.id.image_container);
        deleteIcon = getResources().getDrawable(R.drawable.delete_button_icon);
        storageReference = FirebaseStorage.getInstance().getReference();
        next = view.findViewById(R.id.next);
        db = FirebaseFirestore.getInstance();
        addMedicationActivity = (AddMedicationActivity) requireActivity();
        documentId = getArguments().getString("documentId");
        progressBar = view.findViewById(R.id.progress_circular);
        instructionText = view.findViewById(R.id.add_instruction);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicationActivity.hideAddMedInstructionFragment();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String instruction = instructionText.getText().toString().trim();
                if (!instruction.isEmpty()) {
                    blockFragment();
                    progressBar.setVisibility(View.VISIBLE);
                    addInstructionTextToDb(instruction);
                } else {
                    blockFragment();
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImage(images);
                   // addMedicationActivity.hideAddMedInstructionFragment();
                }
            }
        });


        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.select_image_source);
                builder.setItems(new CharSequence[]{getString(R.string.gallery), getString(R.string.camera)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard(getActivity());
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
    }

    private void addInstructionTextToDb(String instruction) {
        DocumentReference userRef = db.collection("meds").document(documentId);
        userRef.update("instructions", instruction)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (!images.isEmpty()) {
                                uploadImage(images);

                            }else {
                                progressBar.setVisibility(View.GONE);
                                unBlockFragment();
                                addMedicationActivity.hideAddMedInstructionFragment();
                            }
                            System.out.println("instruction добавлена в Firestore");
                        } else {
                            System.out.println("instruction при добавлении ссылки в Firestore");
                        }
                    }
                });
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
                            addImageToContainer(imageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                        addImageToContainer(imageBitmap);
                    }
                    break;
            }
        }
    }

    private void addImageToContainer(Bitmap bitmap) {
        LinearLayout imageLayout = new LinearLayout(requireContext());
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imageView = new ImageView(requireContext());
        images.add(bitmap);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        imageView.setLayoutParams(imageParams);

        Button deleteButton = new Button(requireContext());
        deleteButton.setBackground(deleteIcon);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                50,
                50
        );
        deleteButton.setLayoutParams(buttonParams);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images.remove(bitmap);
                imageContainer.removeView(imageLayout);
            }
        });

        imageLayout.addView(imageView);
        imageLayout.addView(deleteButton);

        imageContainer.addView(imageLayout);

        int width = 200;
        int height = 300;
        imageView.setPadding(0, 0, 0, 0);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, 0, 0);
        imageView.setLayoutParams(layoutParams);
        hideKeyboard(getActivity());
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void uploadImage(ArrayList<Bitmap> images) {
        AtomicInteger uploadCount = new AtomicInteger(0);

        for (int i = 0; i < images.size(); i++) {
            Bitmap imageBitmap = images.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            String fileName = System.currentTimeMillis() + "_" + i + ".jpg";
            StorageReference imageRef = storageReference.child("instructions/" + fileName);

            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Uri downloadUri = task1.getResult();
                            if (downloadUri != null) {
                                imageUrls.add(downloadUri.toString());

                                int count = uploadCount.incrementAndGet();

                                if (count == images.size()) {
                                    saveImageUriToDb(imageUrls);
                                }
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    unBlockFragment();
                    System.out.println("UploadImage Ошибка загрузки изображения ");
                }
            });
        }
    }


    private void saveImageUriToDb(ArrayList<String> imageUrls) {
        DocumentReference userRef = db.collection("meds").document(documentId);
        userRef.update("instructionImgUrls", imageUrls)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        unBlockFragment();
                        if (task.isSuccessful()) {

                            addMedicationActivity.hideAddMedInstructionFragment();
                            System.out.println("Ссылка на изображение добавлена в Firestore");
                        } else {
                            System.out.println("Ошибка при добавлении ссылки в Firestore");
                        }
                    }
                });
    }

    private void blockFragment() {
        addImg.setEnabled(false);
        imageContainer.setEnabled(false);
        next.setEnabled(false);
        instructionText.setEnabled(false);
    }

    private void unBlockFragment() {
        addImg.setEnabled(true);
        imageContainer.setEnabled(true);
        next.setEnabled(true);
        instructionText.setEnabled(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_med_add_instruction, container, false);
    }
}
