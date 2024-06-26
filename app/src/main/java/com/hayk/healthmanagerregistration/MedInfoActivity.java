package com.hayk.healthmanagerregistration;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.BuildConfig;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;


import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;


public class MedInfoActivity extends AppCompatActivity {
    private TextView medName, medForm, medReminders, medTimes, medFood, medInstruction, doseTypeTV;
    private ImageView back, delete;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private LinearLayout medAmount;
    private Button plusCount, minusCount;
    private EditText amountEt;
    private String medId;
    private TextView openInstruction;
    private FirebaseStorage storage;
    private List<String> imgUrls;
    private static final String PREFS_NAME = "language_prefs";
    private static final String LANGUAGE_KEY = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);
        doseTypeTV = findViewById(R.id.dose_type_tv);
        plusCount = findViewById(R.id.plus_count);
        minusCount = findViewById(R.id.minus_count);
        amountEt = findViewById(R.id.amount_et);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        medName = findViewById(R.id.med_name);
        medForm = findViewById(R.id.med_form);
        medReminders = findViewById(R.id.med_reminders);
        medTimes = findViewById(R.id.med_times);
        medAmount = findViewById(R.id.med_amount);
        medFood = findViewById(R.id.med_food);
        medInstruction = findViewById(R.id.med_instruction);
        medId = getIntent().getStringExtra("medId");
        openInstruction = findViewById(R.id.open_instruction);
        storage = FirebaseStorage.getInstance();
        imgUrls = new ArrayList<>();

        openInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                readFirestoreAndCreatePdf();
            }
        });
        isInstructionExist();
        getData(medId);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteVisit(medId);
            }
        });
    }

    private void isInstructionExist() {
        db.collection("meds").document(medId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> imgUrls = (List<String>) document.get("instructionImgUrls");
                                if (imgUrls != null && !imgUrls.isEmpty()) {
                                    String text = getString(R.string.instruction_pdf);
                                    SpannableString content = new SpannableString(text);
                                    content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                                    openInstruction.setText(content);
                                    openInstruction.setTextColor(Color.BLUE);
                                } else {
                                    openInstruction.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }


    private void readFirestoreAndCreatePdf() {
        db.collection("meds").document(medId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                imgUrls = (List<String>) document.get("instructionImgUrls");
                                new DownloadImagesTask(MedInfoActivity.this).execute(imgUrls.toArray(new String[0]));

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getData(String medId) {
        CollectionReference medsCollection = db.collection("meds");

        medsCollection.document(medId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String medNameStr = documentSnapshot.getString("medName");
                            String medFormStr = documentSnapshot.getString("medForm");
                            String medRemindersStr = documentSnapshot.getString("medFrequency");
                            String medInstructionStr = documentSnapshot.getString("instructions");
                            final String[] medCount = {String.valueOf(documentSnapshot.getLong("medCount"))};
                            final String[] doseType = {documentSnapshot.getString("doseType")};
                            String medFoodStr = documentSnapshot.getString("medWithFood");
                            switch (medFormStr) {
                                case "pill":
                                    medFormStr = getString(R.string.pill);
                                    break;
                                case "injection":
                                    medFormStr = getString(R.string.injection);
                                    break;
                                case "solution":
                                    medFormStr = getString(R.string.solution);
                                    break;
                                case "drops":
                                    medFormStr = getString(R.string.drops);
                                    break;
                                case "powder":
                                    medFormStr = getString(R.string.powder);
                                    break;
                            }
                            switch (doseType[0]) {
                                case "pill(s)":
                                    doseType[0] = getString(R.string.pill_s);
                                    break;
                                case "mL":
                                    doseType[0] = getString(R.string.ml);
                                    break;
                                case "Syringe(s)":
                                    doseType[0] = getString(R.string.syringe_s);
                                    break;

                                case "Unit":
                                    doseType[0] = getString(R.string.unit);
                                    break;
                                case "Ampules(s)":
                                    doseType[0] = getString(R.string.ampules_s);
                                    break;
                                case "Vial(s)":
                                    doseType[0] = getString(R.string.vial_s);
                                    break;
                                case "Cup(s)":
                                    doseType[0] = getString(R.string.cup_s);
                                    break;
                                case "Drop(s)":
                                    doseType[0] = getString(R.string.drop_s);
                                    break;
                                case "Packet(s)":
                                    doseType[0] = getString(R.string.packet_s);
                                    break;
                                case "Gram(s)":
                                    doseType[0] = getString(R.string.gram_s);
                                    break;
                                case "Tablespoon(s)":
                                    doseType[0] = getString(R.string.tablespoon_s);
                                    break;
                                case "Teaspoon(s)":
                                    doseType[0] = getString(R.string.teaspoon_s);
                                    break;
                            }
                            medName.setText(medNameStr);
                            medForm.setText(getString(R.string.med_form) + medFormStr);
                            String reminderText = getString(R.string.reminders);
                            String medListText = getString(R.string.medication_list);

                            switch (medRemindersStr) {
                                case "onceDay":
                                    String medTime = formatTime(documentSnapshot.getString("medTime"));
                                    String doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + " " + doseType[0];
                                    reminderText += getResources().getString(R.string.once_a_day);
                                    break;
                                case "twiceDay":
                                    String medFirstTime = formatTime(documentSnapshot.getString("medFirstTime"));
                                    String doseFirstCount = documentSnapshot.getString("firstDoseCount");
                                    String medSecondTime = formatTime(documentSnapshot.getString("medSecondTime"));
                                    String doseSecondCount = documentSnapshot.getString("secondDoseCount");
                                    medListText += medFirstTime + " " + doseFirstCount + doseType[0] + ", " + medSecondTime + " " + doseSecondCount + doseType[0];
                                    reminderText += getResources().getString(R.string.twice_a_day);
                                    break;
                                case "everyOtherDay":
                                    medTime = formatTime(documentSnapshot.getString("medTime"));
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    reminderText += getResources().getString(R.string.every_other_day);
                                    break;
                                case "everyXDays":
                                    medTime = formatTime(documentSnapshot.getString("medTime"));
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xDays = documentSnapshot.getString("everyXDays");
                                    reminderText += getResources().getString(R.string.every) + " " + xDays + " " + getString(R.string.Days);
                                    break;
                                case "specificDays":
                                    medTime = formatTime(documentSnapshot.getString("medTime"));
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    ArrayList<String> days = (ArrayList<String>) documentSnapshot.get("daysOfWeek");
                                    reminderText += getString(R.string.on);
                                    for (String day : days) {
                                        switch (day) {
                                            case "sunday":
                                                day = getResources().getString(R.string.sunday);
                                                break;
                                            case "monday":
                                                day = getResources().getString(R.string.monday);
                                                break;
                                            case "tuesday":
                                                day = getResources().getString(R.string.tuesday);
                                                break;
                                            case "wednesday":
                                                day = getResources().getString(R.string.wednesday);
                                                break;
                                            case "friday":
                                                day = getResources().getString(R.string.friday);
                                                break;
                                            case "saturday":
                                                day = getResources().getString(R.string.saturday);
                                                break;
                                            case "thursday":
                                                day = getResources().getString(R.string.thursdays);
                                                break;
                                        }
                                        reminderText += day + " ";
                                    }
                                    break;
                                case "everyXWeeks":
                                    medTime = formatTime(documentSnapshot.getString("medTime"));
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xWeeks = documentSnapshot.getString("everyXWeeks");
                                    if (xWeeks.equals("1")) {
                                        reminderText += getString(R.string.once_a_week);
                                    } else {
                                        reminderText += getString(R.string.Every) + " " + xWeeks + " " + getString(R.string.weeks);
                                    }
                                    break;
                                case "everyXMonths":
                                    medTime = formatTime(documentSnapshot.getString("medTime"));
                                    doseCount = documentSnapshot.getString("doseCount");
                                    medListText += medTime + " " + doseCount + doseType[0];
                                    String xMonths = documentSnapshot.getString("everyXMonths");
                                    if (xMonths.equals("1")) {
                                        reminderText += getString(R.string.once_a_month);
                                    } else {
                                        reminderText += getString(R.string.Every) + " " + xMonths + " " + getString(R.string.months);
                                    }
                                    break;
                                case "moreTimes":
                                    String howTimes = documentSnapshot.getString("howTimesDay");
                                    reminderText += howTimes + " " + getString(R.string.times_in_day);
                                    ArrayList<String> times = (ArrayList<String>) documentSnapshot.get("times");
                                    ArrayList<String> doses = (ArrayList<String>) documentSnapshot.get("doses");
                                    for (int i = 0; i < times.size(); i++) {
                                        String formattedTime = formatTime(times.get(i));
                                        if (i != times.size() - 1) {
                                            medListText += formattedTime + " " + doses.get(i) + " " + doseType[0] + ", ";
                                        } else {
                                            medListText += formattedTime + " " + doses.get(i) + " " + doseType[0];
                                        }
                                    }
                                    break;
                            }

                            medReminders.setText(reminderText);
                            medTimes.setText(medListText);
                            if (medInstructionStr == null) {
                                medInstruction.setVisibility(View.GONE);
                            } else {
                                medInstruction.setText(getString(R.string.instruction) + medInstructionStr);
                            }
                            if (medCount[0].equals("null")) {
                                medAmount.setVisibility(View.GONE);
                            } else {
                                medAmount.setVisibility(View.VISIBLE);
                                amountEt.setCursorVisible(false);
                                amountEt.setText(medCount[0]);
                                doseTypeTV.setText(doseType[0]);
                                progressBar.setVisibility(View.GONE);
                                plusCount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int count;
                                        if (medCount[0].isEmpty()) {
                                            count = 1;
                                        } else {
                                            count = Integer.parseInt(medCount[0]);
                                            count++;
                                        }

                                        if (count > 99999) count = 99999;
                                        medCount[0] = String.valueOf(count);
                                        amountEt.setText(String.valueOf(count));
                                        setNewCount(medCount[0]);

                                    }
                                });
                                minusCount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int count;
                                        if (medCount[0].isEmpty()) {
                                            count = 0;
                                        } else {
                                            count = Integer.parseInt(medCount[0]);
                                            count--;
                                        }

                                        if (count < 0) count = 0;
                                        medCount[0] = String.valueOf(count);
                                        amountEt.setText(String.valueOf(count));
                                        setNewCount(medCount[0]);

                                    }
                                });
                                amountEt.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        medCount[0] = amountEt.getText().toString();
                                        if (!medCount[0].isEmpty()) {

                                            setNewCount(medCount[0]);
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {

                                    }
                                });


                            }
                            if (medFoodStr == null) {
                                medFood.setVisibility(View.GONE);
                            } else {
                                String text;
                                if (medFoodStr.equals("fromEating")) {
                                    text = getResources().getString(R.string.from_eating);
                                } else if (medFoodStr.equals("beforeEating")) {
                                    text = getString(R.string.before_eating);

                                } else {
                                    text = getString(R.string.after_eating);
                                }
                                medFood.setText(getString(R.string.you_should_take_it) + " " + text.toLowerCase());
                            }

                        } else {
                            progressBar.setVisibility(View.GONE);

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }

    private void setNewCount(String s) {
        CollectionReference med = db.collection("meds");

        Map<String, Object> data = new HashMap<>();
        data.put("medCount", Integer.parseInt(s));

        med.document(medId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        System.out.println("failure");
                    }
                });
    }

    private String formatTime(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return String.format("%02d:%02d", hours, minutes);
    }

    private void deleteVisit(String medId) {
        db.collection("meds").document(medId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userMeds").document(medId)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Документ успешно удален из Firestore"))
                .addOnFailureListener(e -> System.err.println("Ошибка при удалении документа из Firestore: " + e));
        finish();
    }

    private class DownloadImagesTask extends AsyncTask<String, Void, List<Bitmap>> {

        private Context context;

        public DownloadImagesTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Bitmap> doInBackground(String... urls) {
            List<Bitmap> bitmaps = new ArrayList<>();
            for (String url : urls) {
                try {
                    URL imageUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    bitmaps.add(bitmap);
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            createPdf(bitmaps);
        }

        private void createPdf(List<Bitmap> bitmaps) {
            writePdfFile(MedInfoActivity.this, bitmaps);

        }

        private void openPdf(Context context, Uri uri) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            progressBar.setVisibility(View.GONE);
        }

        private void writePdfFile(Context context, List<Bitmap> bitmaps) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(byteArrayOutputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument, PageSize.A4);
                pdfDocument.setDefaultPageSize(PageSize.A4);

                for (Bitmap bitmap : bitmaps) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = new Image(com.itextpdf.io.image.ImageDataFactory.create(stream.toByteArray()));
                    image.scaleToFit(PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
                    document.add(new Paragraph().add(image));
                }

                document.close();

                File tempFile = File.createTempFile("temp_pdf", ".pdf", context.getCacheDir());
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(byteArrayOutputStream.toByteArray());
                fos.close();

                Uri uri = FileProvider.getUriForFile(context, "com.hayk.healthmanagerregistration.fileprovider", tempFile);
                openPdf(context, uri);

            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        String language = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "en");
        super.attachBaseContext(LanguageManager.updateBaseContextLocale(newBase, new Locale(language)));
    }
}
