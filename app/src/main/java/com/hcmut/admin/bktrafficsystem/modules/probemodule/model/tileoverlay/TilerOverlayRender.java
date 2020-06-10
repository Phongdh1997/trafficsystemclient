package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class TilerOverlayRender {
    private GoogleMap map;

    private TileOverlay statusTileOverlay;
    private TrafficTileProvider trafficTileProvider;

    public TilerOverlayRender(GoogleMap map, Context context) {
        this.map = map;
        trafficTileProvider = new TrafficTileProvider(context);
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(trafficTileProvider));
        notifyDataChange();
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public void notifyDataChange() {

    }
}
