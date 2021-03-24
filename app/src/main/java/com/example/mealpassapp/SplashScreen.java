package com.example.mealpassapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealpassapp.model.UsersModel;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread obj = new Thread() {
            public void run() {
                try {
                    sleep(1400);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        boolean statusUser = sharedPref.getBoolean("StatusUser", false);
                        boolean statusFood = sharedPref.getBoolean("StatusFood", false);

                        if (statusUser) {
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (statusFood) {
                            Intent intent = new Intent(getApplicationContext(), FoodSellerActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(SplashScreen.this, LoginScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };obj.start();
    }
}
