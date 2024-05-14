package com.finals.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Splash extends AppCompatActivity {
    AnimationDrawable Logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        ImageView ivSplash = findViewById(R.id.ivSplash);
        Intent intent = new Intent(this, Home.class);

        ivSplash.setBackgroundResource(R.drawable.logo_animation);
        Logo = (AnimationDrawable) ivSplash.getBackground();

        // Splash screen (1.5s)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();

            }
        }, 4500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Logo.start();
    }
}