package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.util.CustomTileProvider;

import java.util.ArrayList;

/**
 * Đại diện cho nguồn dữ liệu status sẽ được render lên TileOverlay
 */
public class StatusOverlayRender {
    private GoogleMap map;

    private TileOverlay statusTileOverlay;
    private CustomTileProvider statusTileProvider;

    public StatusOverlayRender(GoogleMap map) {
        this.map = map;
        statusTileProvider = new CustomTileProvider(new ArrayList<ArrayList<LatLng>>(), Color.RED);
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(statusTileProvider));
    }

    public void updateDataSource(ArrayList<ArrayList<LatLng>> source) {
        statusTileOverlay.clearTileCache();
        statusTileProvider.updateSource(source);
        Log.e("change ", "ch");
    }
}
