package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;

import java.util.Timer;
import java.util.TimerTask;

public class StatusRender {

    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 60000 * 3; // 3 minus

    private boolean isTimerRunning;

    private MutableLiveData<StatusRenderEvent> statusRenderEventLiveData;

    private Timer statusRenderTimer;

    public StatusRender() {
        isTimerRunning = false;
        statusRenderEventLiveData = new MutableLiveData<>();
    }

    public LiveData<StatusRenderEvent> getStatusRenderEvent () {
        return statusRenderEventLiveData;
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
