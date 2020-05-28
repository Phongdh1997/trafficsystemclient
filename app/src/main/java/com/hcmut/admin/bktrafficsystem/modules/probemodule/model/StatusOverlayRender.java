package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.CustomTileProvider;

/**
 * Đại diện cho nguồn dữ liệu status sẽ được render lên TileOverlay
 */
public class StatusOverlayRender {
    private GoogleMap map;

    private TileOverlay statusTileOverlay;
    private CustomTileProvider statusTileProvider;

    public StatusOverlayRender(GoogleMap map, Context context) {
        this.map = map;
        statusTileProvider = new CustomTileProvider(context);
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(statusTileProvider));
        notifyDataChange();
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public void notifyDataChange() {
        statusTileOverlay.clearTileCache();
        statusTileProvider.clearTileDataCached();
    }
}
