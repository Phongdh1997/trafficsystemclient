package com.hcmut.admin.bktrafficsystem.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {
    public static long time;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        time = Calendar.getInstance().getTimeInMillis();
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
