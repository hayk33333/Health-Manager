package com.hayk.healthmanagerregistration;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class NoNetworkFragmentManager extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment currentFragment = fragmentManager.findFragmentById(android.R.id.content);


    public void showNoNetworkFragment(final FragmentActivity activity) {
        if (currentFragment instanceof ForgotPasswordFragment) {
            // Ваш фрагмент открыт в данный момент
            ForgotPasswordFragment yourFragment = (ForgotPasswordFragment) currentFragment;
            yourFragment.blockFragment();
        } else {
            // Фрагмент с другим классом открыт в данный момент
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoNetworkFragment noNetworkFragment = new NoNetworkFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, noNetworkFragment)
                        .commitAllowingStateLoss();
            }
        });
    }



    public void hideNoNetworkFragment(FragmentActivity activity) {
        NoNetworkFragment noNetworkFragment = (NoNetworkFragment) activity.getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (noNetworkFragment != null && !noNetworkFragment.isDetached()) {
            activity.getSupportFragmentManager().beginTransaction().remove(noNetworkFragment).commitAllowingStateLoss();
        }
    }
}
