package com.hcmut.admin.bktrafficsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
