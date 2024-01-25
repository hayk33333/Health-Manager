package com.hayk.healthmanagerregistration;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseUser;

public class NetworkCheckThread extends Thread {

    private FragmentActivity activity;
    boolean isFragmentShow = false;
    static boolean isRunning = true;


    public NetworkCheckThread(FragmentActivity activity) {
        this.activity = activity;
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                // Выполняем проверку каждые 5 секунд (можете изменить по своему усмотрению)
                Thread.sleep(2000);
                NetworkUtils.checkInternetConnectivity(new NetworkUtils.NetworkCheckListener() {
                    @Override
                    public void onNetworkCheckCompleted(boolean isInternetAvailable) {
                        if (isInternetAvailable) {
                            System.out.println("Сеть доступна");
                            if (isFragmentShow) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        NoNetworkFragmentManager noNetworkFragmentManager = new NoNetworkFragmentManager();
                                        noNetworkFragmentManager.hideNoNetworkFragment(activity);
                                        isFragmentShow = false;


                                    }
                                });
                            }
                        } else {
                            // Ваш код, который будет выполнен, если нет интернета
                            if (!isFragmentShow) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NoNetworkFragmentManager noNetworkFragmentManager = new NoNetworkFragmentManager();
                                        noNetworkFragmentManager.showNoNetworkFragment(activity);
                                        isFragmentShow = true;


                                    }
                                });
                            }
                            System.out.println("Сеть недоступна");
                        }
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void stopThread(){
        isRunning = false;
    }
    public void startThread(){
        isRunning = true;
    }

}
