package com.example.notime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;


public class SplashScreen extends AppCompatActivity {

    ImageView bit_logo;
    TextView no_time_text;
    LottieAnimationView timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        bit_logo = findViewById(R.id.bit_logo);
        no_time_text = findViewById(R.id.no_time_text);
        timer = findViewById(R.id.timer);

        Animation text_animation = AnimationUtils.loadAnimation(this, R.anim.text_animation);
        Animation logo_animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);

        no_time_text.setAnimation(text_animation);
        bit_logo.setAnimation(logo_animation);
        timer.setAnimation(logo_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(splashIntent);
//              CustomIntent.customType(SplashScreen.this, "fadein-to-fadeout");
                finish();
            }
        },3000);

    }
}
