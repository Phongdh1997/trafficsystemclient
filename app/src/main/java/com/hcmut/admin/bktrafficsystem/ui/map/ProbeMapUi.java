package com.hcmut.admin.bktrafficsystem.ui.map;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.GoogleMapMemoryManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.RefreshStatusHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.TilerOverlayRender;

import org.jetbrains.annotations.NotNull;

public class ProbeMapUi {

    /**
     * external view
     */
    private GoogleMap gmaps;
    private TilerOverlayRender tilerOverlayRender;
    private GoogleMapMemoryManager mapMemoryManager;
    private RefreshStatusHandler refreshStatusHandler;

    public ProbeMapUi(Context context,  @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        tilerOverlayRender = new TilerOverlayRender(gmaps, context);
        mapMemoryManager = GoogleMapMemoryManager.getInstance(mapFragment);
        refreshStatusHandler = new RefreshStatusHandler(tilerOverlayRender);
    }

    public void setupRenderStatus () {
        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mapMemoryManager.onMapMove(gmaps.getCameraPosition().zoom);
            }
        });
        refreshStatusHandler.startStatusRenderTimer();
    }

    public void startStatusRenderTimer () {
        refreshStatusHandler.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer () {
        refreshStatusHandler.stopStatusRenderTimer();
    }
}
