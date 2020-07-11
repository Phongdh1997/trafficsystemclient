package com.hcmut.admin.bktrafficsystem.business.trafficmodule;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.bktrafficsystem.business.GoogleMapMemoryManager;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.RefreshStatusHandler;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender.MatrixStatusRenderImpl;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay.TilerOverlayRender;

import org.jetbrains.annotations.NotNull;

public class TrafficRenderModule {
    public static float MAX_ZOOM_LEVEL = 18f;

    /**
     * external view
     */
    private GoogleMap gmaps;
    private TilerOverlayRender tilerOverlayRender;
    private GoogleMapMemoryManager mapMemoryManager;
    private RefreshStatusHandler refreshStatusHandler;
    private StatusRender statusRender;

    public TrafficRenderModule(Context context, @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        tilerOverlayRender = new TilerOverlayRender(gmaps, context);
        mapMemoryManager = new GoogleMapMemoryManager(mapFragment);
        refreshStatusHandler = new RefreshStatusHandler();
        statusRender = new MatrixStatusRenderImpl(gmaps, context);
        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mapMemoryManager.onMapMove(gmaps.getCameraPosition().zoom);
                statusRender.onCameraMoving(gmaps);
            }
        });
        refreshStatusHandler.setOverlayRender(tilerOverlayRender);
    }

    /**
     * API to turn on/off traffic status render
     * @param enable: true value to enable, false value to disable
     */
    public void setTrafficEnable(boolean enable) {
        // refreshStatusHandler.setTrafficEnable(enable);
    }

    public void startStatusRenderTimer () {
        refreshStatusHandler.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer () {
        refreshStatusHandler.stopStatusRenderTimer();
    }
}
