package com.hayk.healthmanagerregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AddMedicationActivity extends AppCompatActivity {
    TextView message;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        message = findViewById(R.id.med_activity_message);
        icon = findViewById(R.id.icon);
        showMedNameFragment();

    }

    public void showMedNameFragment() {
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        MedNameFragment medNameFragment = new MedNameFragment();
        icon.setImageResource(R.drawable.med_interrogative);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medNameFragment)
                .commit();
    }

    public void showMedFormFragment() {
        icon.setImageResource(R.drawable.med_form);
        message.setText(R.string.what_form_is_the_med);
        MedFormFragment medFormFragment = new MedFormFragment();
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, medFormFragment)
                .commit();
    }

    public void hideMedFormFragment() {
        icon.setImageResource(R.drawable.med_interrogative);
        message.setText(R.string.what_med_would_you_like_to_add);
        MedFormFragment medFormFragment = (MedFormFragment) getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (medFormFragment != null && !medFormFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction().remove(medFormFragment).commit();
        }

    }
}