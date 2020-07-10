package com.hcmut.admin.bktrafficsystem.modules.data.model;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.modules.data.model.tile.CustomTileProvider;
import com.hcmut.admin.bktrafficsystem.modules.data.model.tile.TileBitmapRender;

/**
 * Đại diện cho nguồn dữ liệu status sẽ được render lên TileOverlay
 */
@Deprecated
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

    public LiveData<TileBitmapRender> getTileBitmapLiveData () {
        return statusTileProvider.getTileBitmapLiveData();
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public void notifyDataChange() {
        statusTileOverlay.clearTileCache();
        statusTileProvider.clearTileDataCached();
    }
}
