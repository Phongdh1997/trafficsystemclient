package com.hcmut.admin.bktrafficsystem.modules.probemodule.service;

import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.SleepWakeupLocationService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.LocationCollectionManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.TrafficNotificationFactory;

public class AppForegroundService extends Service {

    private static final int SERVICE_ID = 110;

    public static final int SERVICE_STOP_REQUEST_CODE = 111;
    public static final String STOP_FOREGROUND_ACTION = "com.example.traffic.service.LocationService.STOP_FOREGROUND_ACTION";

    private LocationCollectionManager locationCollectionManager;
    private HandlerThread locationHandlerThread;
    private static MutableLiveData<Boolean> movingStateLiveData = new MutableLiveData<>();

    public static LiveData<Boolean> getMovingStateLiveData () {
        return movingStateLiveData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            startForeground(SERVICE_ID, TrafficNotificationFactory
                    .getInstance(getApplicationContext())
                    .getForegroundServiceNotification(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // tracking user location
        locationHandlerThread = new HandlerThread("Location Listener thread");
        locationHandlerThread.start();
        locationCollectionManager = LocationCollectionManager.getInstance(getApplicationContext());
        locationCollectionManager.beginTraceLocation(locationHandlerThread.getLooper());
        locationCollectionManager.setStopServiceEvent(new SleepWakeupLocationService.StopServiceEvent() {
            @Override
            public void onStop() {
                stopForeground(true);
                stopSelf();
            }
        });
        movingStateLiveData.postValue(true);
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
        try {
            locationCollectionManager.endTraceLocation();
            locationHandlerThread.quitSafely();
            locationCollectionManager = null;
            locationHandlerThread = null;
        } catch (Exception e) {}
        movingStateLiveData.postValue(false);
        Log.e("Service", "stop");
    }
}
