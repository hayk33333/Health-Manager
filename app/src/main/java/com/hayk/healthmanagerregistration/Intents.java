package com.hayk.healthmanagerregistration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Intents {

    Activity activity;
    public Intents(Activity activity){
        this.activity = activity;
    }
    public void RegistrationActivity(){
        NetworkCheckThread.stopThread();
        Intent intent = new Intent(activity,RegistrationActivity.class);
        activity.startActivity(intent);
    }
    public void LoginActivity(){
        NetworkCheckThread.stopThread();
        Intent intent = new Intent(activity,LoginActivity.class);
        activity.startActivity(intent);
    }
    public void MainActivity(){
        NetworkCheckThread.stopThread();
        Intent intent = new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
    }
}
