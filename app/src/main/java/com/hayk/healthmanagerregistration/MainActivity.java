package com.hayk.healthmanagerregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    NetworkCheckThread networkCheckThread = new NetworkCheckThread(this);
    Intents intents = new Intents(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkCheckThread.startThread();
        networkCheckThread.start();
        intents.LoginActivity();
    }
}