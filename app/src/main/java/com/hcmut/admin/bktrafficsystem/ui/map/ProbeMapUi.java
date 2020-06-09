package com.hcmut.admin.bktrafficsystem.ui.map;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.RefreshStatusHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.MatrixStatusRenderImpl;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import org.jetbrains.annotations.NotNull;

public class ProbeMapUi {

    /**
     * external view
     */
    private GoogleMap gmaps;
    private StatusRender statusRender;
    private RefreshStatusHandler refreshStatusHandler;

    public ProbeMapUi(Context context,  @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        statusRender = new MatrixStatusRenderImpl(gmaps, context, mapFragment);
        refreshStatusHandler = new RefreshStatusHandler(gmaps, statusRender);
        refreshStatusHandler.startRefreshStatus();
    }

    public void setupRenderStatus () {
        gmaps.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                statusRender.onCameraMoving(gmaps);
            }
        });

        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                statusRender.onCameraMoving(gmaps);
            }
        });
    }

    public void refreshRenderStatus () {
        statusRender.refreshRenderStatus(TileCoordinates.getCenterTile(gmaps));
    }

    public void onStop() {
        refreshStatusHandler.stopRefreshStatus();
    }
}
