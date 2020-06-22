package com.hcmut.admin.bktrafficsystem;

import android.app.Activity;
import android.app.Application;

import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

public class MyApplication extends Application {

    private MapActivity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MapActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(MapActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

}