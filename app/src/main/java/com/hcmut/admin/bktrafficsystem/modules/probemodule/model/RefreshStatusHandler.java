package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.TilerOverlayRender;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class RefreshStatusHandler {
    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 60000 * 1; // default 3 minus

    private boolean isTimerRunning = false;
    private Timer statusRenderTimer;
    private TilerOverlayRender overlayRender;

    public RefreshStatusHandler(TilerOverlayRender overlayRender) {
        this.overlayRender = overlayRender;
    }

    public void startStatusRenderTimer() {
        if (isTimerRunning) return;
        statusRenderTimer = new Timer();
        TimerTask renderTimerTask = new TimerTask() {
            @Override
            public void run() {
                overlayRender.notifyDataChange();
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
