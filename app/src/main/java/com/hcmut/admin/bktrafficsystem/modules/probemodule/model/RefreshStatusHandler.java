package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.TileRenderHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import java.lang.ref.WeakReference;

public class RefreshStatusHandler {
    private StatusRender statusRender;
    private long timeFrame = 60000 * 1; // default 3 minus
    private WeakReference<GoogleMap> googleMapWeakReference;
    private boolean isRunning = false;

    public RefreshStatusHandler(GoogleMap googleMap, StatusRender statusRender) {
        this.statusRender = statusRender;
        googleMapWeakReference = new WeakReference<>(googleMap);
    }

    public void startRefreshStatus () {
        isRunning = true;
        TileRenderHandler.mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doRefreshStatus();
                if (isRunning) {
                    TileRenderHandler.mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doRefreshStatus();
                        }
                    }, timeFrame);
                }
            }
        }, timeFrame);
    }

    public void stopRefreshStatus() {
        isRunning = false;
    }

    private void doRefreshStatus () {
        GoogleMap googleMap = googleMapWeakReference.get();
        if (googleMap != null) {
            statusRender.refreshRenderStatus(TileCoordinates.getCenterTile(googleMap));
            Log.e("refresh", "refresh");
        }
    }
}
