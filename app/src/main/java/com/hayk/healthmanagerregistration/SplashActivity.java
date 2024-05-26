package com.hayk.healthmanagerregistration;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private View image;
    private ImageView logo_text;
    private TextView text;
    private Intents intents = new Intents(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image = findViewById(R.id.imageView);
        logo_text = findViewById(R.id.logo_text);
        text = findViewById(R.id.text);

        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        image.startAnimation(slideUpAnimation);

        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                slideText();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void slideText() {
        Animation slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        Animation slideLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        logo_text.setVisibility(View.VISIBLE);
        //text.setVisibility(View.VISIBLE);
        logo_text.setAnimation(slideRightAnimation);
        text.setAnimation(slideLeftAnimation);
        slideRightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                intents.MainActivity();
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
}
