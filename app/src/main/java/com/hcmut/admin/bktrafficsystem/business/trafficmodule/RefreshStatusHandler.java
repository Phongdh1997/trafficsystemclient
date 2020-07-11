package com.hcmut.admin.bktrafficsystem.business.trafficmodule;

import com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay.TilerOverlayRender;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshStatusHandler {
    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 60000 * 2; // default 3 minus

    private boolean trafficEnable = true;
    private boolean isTimerRunning = false;
    private Timer statusRenderTimer;
    private TilerOverlayRender overlayRender;

    public RefreshStatusHandler() {

    }

    public void setOverlayRender (TilerOverlayRender overlayRender) {
        this.overlayRender = overlayRender;
    }

    public void setTrafficEnable(boolean trafficEnable) {
        this.trafficEnable = trafficEnable;
    }

    public void startStatusRenderTimer() {
        if (isTimerRunning) return;
        statusRenderTimer = new Timer();
        TimerTask renderTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (trafficEnable && overlayRender != null) {
                    overlayRender.notifyDataChange();
                }
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