package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile;

import java.util.List;

import android.content.Context;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

public class CustomTileProvider implements TileProvider {

    private TrafficTileLoader trafficTileLoader;
    private TileBitmap tileBitmapHelper = new TileBitmap();

    public CustomTileProvider(Context context) {
        trafficTileLoader = new TrafficTileLoader(context);
    }

    public void clearTileDataCached() {
        trafficTileLoader.clearTileDataCached();

    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        if (zoom < 15 || zoom > 21) {
            return NO_TILE;
        }
        List<StatusRenderDataEntity> statusDatas = null;
        try {
            statusDatas = trafficTileLoader.loadTileDataFromLocal(TileCoordinates.getTileCoordinates(x, y, zoom));
        } catch (Exception e){}
        return tileBitmapHelper.getTileWithScale(x, y, zoom, statusDatas);
    }
}