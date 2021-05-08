package com.example.viduratest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.example.viduratest.Utills.Utill;

public class SplashActivity extends AppCompatActivity {

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                String userKey = Utill.getUserPref(mContext);
                if(!userKey.equalsIgnoreCase("")){
                    Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
                    intent.putExtra("key",userKey);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);

    }
}