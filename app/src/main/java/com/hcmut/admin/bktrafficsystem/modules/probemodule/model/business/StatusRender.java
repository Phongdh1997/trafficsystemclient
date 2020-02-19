package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusRender {

    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 30000;

    private boolean isTimerRunning;

    private LiveData<List<PolylineOptions>> statusRenderPolylineOptionsLiveData;
    private MutableLiveData<StatusRenderEvent> statusRenderEventLiveData;

    private StatusRepositoryService statusRepositoryService;
    private Timer statusRenderTimer;

    public StatusRender() {
        isTimerRunning = false;
        statusRenderEventLiveData = new MutableLiveData<>();
        statusRepositoryService = new StatusRemoteRepository();
        statusRenderPolylineOptionsLiveData = statusRepositoryService.getStatusRenderData();
    }

    public LiveData<List<PolylineOptions>> getStatusRenderData() {
        return statusRenderPolylineOptionsLiveData;
    }

    public LiveData<StatusRenderEvent> getStatusRenderEvent () {
        return statusRenderEventLiveData;
    }

    public void loadTrafficStatus(UserLocation userLocation, double zoom) {
        Log.e("traffic", "load, user location: " + userLocation);
        statusRepositoryService.loadStatusRenderData(userLocation, zoom);
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
}
