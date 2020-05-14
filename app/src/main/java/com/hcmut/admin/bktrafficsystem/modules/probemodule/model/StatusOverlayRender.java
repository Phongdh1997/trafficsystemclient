package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.util.CustomTileProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho nguồn dữ liệu status sẽ được render lên TileOverlay
 */
public class StatusOverlayRender {
    private GoogleMap map;

    private TileOverlay statusTileOverlay;
    private CustomTileProvider statusTileProvider;

    public StatusOverlayRender(GoogleMap map) {
        this.map = map;
        statusTileProvider = new CustomTileProvider(new ArrayList<StatusRenderData>());
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(statusTileProvider));
    }

    /**
     * Add new data source to old data source, don't clear Tile Cache
     * @param statusDataSource
     */
    public void addDataSource(List<StatusRenderData> statusDataSource) {
        statusTileProvider.addDataSource(statusDataSource);
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public void notifyDataChange() {
        statusTileOverlay.clearTileCache();
    }

    /**
     * clear render and data source
     */
    public void clearRender() {
        statusTileProvider.clearDataSource();
        statusTileOverlay.clearTileCache();
    }
}
