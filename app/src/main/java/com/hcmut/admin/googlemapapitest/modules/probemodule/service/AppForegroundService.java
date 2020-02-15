package com.hcmut.admin.googlemapapitest.modules.probemodule.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hcmut.admin.googlemapapitest.modules.probemodule.utils.LocationCollectionManager;
import com.hcmut.admin.googlemapapitest.modules.probemodule.utils.NotificationManager;

public class AppForegroundService extends Service {

    private static final int SERVICE_ID = 110;

    public static final int SERVICE_STOP_REQUEST_CODE = 111;
    public static final String STOP_FOREGROUND_ACTION = "com.example.traffic.service.LocationService.STOP_FOREGROUND_ACTION";

    private LocationCollectionManager locationCollectionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            startForeground(SERVICE_ID, NotificationManager
                    .getInstance(getApplicationContext())
                    .getForegroundServiceNotification(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // tracking user location
        locationCollectionManager = LocationCollectionManager.getInstance(getApplicationContext());
        locationCollectionManager.beginTraceLocation();
        Log.e("app service", "created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STOP_FOREGROUND_ACTION.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationCollectionManager.endTraceLocation();
        locationCollectionManager = null;
    }
}
