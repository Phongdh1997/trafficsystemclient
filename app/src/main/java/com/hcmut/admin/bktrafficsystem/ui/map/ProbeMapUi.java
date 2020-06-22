package com.hcmut.admin.bktrafficsystem.ui.map;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.GoogleMapMemoryManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.RefreshStatusHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.MatrixStatusRenderImpl;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.TilerOverlayRender;

import org.jetbrains.annotations.NotNull;

public class ProbeMapUi {
    public static float MAX_ZOOM_LEVEL = 18f;

    /**
     * external view
     */
    private GoogleMap gmaps;
    private TilerOverlayRender tilerOverlayRender;
    private GoogleMapMemoryManager mapMemoryManager;
    private RefreshStatusHandler refreshStatusHandler;
    private StatusRender statusRender;

    public ProbeMapUi(Context context, @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        tilerOverlayRender = new TilerOverlayRender(gmaps, context);
        mapMemoryManager = GoogleMapMemoryManager.getInstance(mapFragment);
        refreshStatusHandler = new RefreshStatusHandler();
        statusRender = new MatrixStatusRenderImpl(gmaps, context);
    }

    public void setupRenderStatus () {
        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mapMemoryManager.onMapMove(gmaps.getCameraPosition().zoom);
                //statusRender.onCameraMoving(gmaps);
            }
        });
        refreshStatusHandler.startStatusRenderTimer();
        refreshStatusHandler.setOverlayRender(tilerOverlayRender);
    }

    public void startStatusRenderTimer () {
        refreshStatusHandler.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer () {
        refreshStatusHandler.stopStatusRenderTimer();
    }
}
