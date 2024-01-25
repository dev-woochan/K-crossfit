package com.example.k_crossfit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_crossfit.login.LoginActivity;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private TextView text1;
    private TextView text2;
    private TextView loading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        text1 = findViewById(R.id.textview_splash_1);
        text2 = findViewById(R.id.textview_splash_2);
        loading = findViewById(R.id.textview_splash_loading);

        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // 백그라운드에서 작업할것들
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("쓰레드 장애", "run: " + e);
                }
                handler.post(() -> {
                    text1.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d("쓰레드 장애", "run: " + e);
            }
            handler.post(()->{
                loading.setVisibility(View.VISIBLE);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d("쓰레드 장애", "run: " + e);
            }
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }
}
