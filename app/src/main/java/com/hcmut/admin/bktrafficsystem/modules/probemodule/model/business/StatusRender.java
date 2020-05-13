package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusRender {

    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 30000;

    private boolean isTimerRunning;

    private LiveData<GeoJsonLayer> statusRenderGeoJsonLayerLiveData;
    private MutableLiveData<StatusRenderEvent> statusRenderEventLiveData;

    private StatusRepositoryService statusRepositoryService;
    private Timer statusRenderTimer;

    public StatusRender() {
        isTimerRunning = false;
        statusRenderEventLiveData = new MutableLiveData<>();
        statusRepositoryService = new StatusRemoteRepository();
        statusRenderGeoJsonLayerLiveData = statusRepositoryService.getStatusRenderData();
    }

    public LiveData<GeoJsonLayer> getStatusRenderData() {
        return statusRenderGeoJsonLayerLiveData;
    }

    public LiveData<StatusRenderEvent> getStatusRenderEvent () {
        return statusRenderEventLiveData;
    }

    public void loadTrafficStatus(UserLocation userLocation, double zoom, GoogleMap map) {
        Log.e("traffic", "load, user location: " + userLocation);
        statusRepositoryService.loadStatusRenderData(userLocation, zoom, new WeakReference<GoogleMap>(map));
    }

    public void startStatusRenderTimer() {
        if (isTimerRunning) return;
        statusRenderTimer = new Timer();
        TimerTask renderTimerTask = new TimerTask() {
            @Override
            public void run() {
                statusRenderEventLiveData.postValue(new StatusRenderEvent());
            }
        };
        statusRenderTimer.schedule(renderTimerTask, TIMER_DELAY, TIMER_PERIOD);
        isTimerRunning = true;
    }

    public void stopStatusRenderTimer() {
        try {
            statusRenderTimer.cancel();
            isTimerRunning = false;
        } catch (Exception e) {}
    }

    public void triggerRender() {
        statusRenderEventLiveData.postValue(new StatusRenderEvent());
    }
}
