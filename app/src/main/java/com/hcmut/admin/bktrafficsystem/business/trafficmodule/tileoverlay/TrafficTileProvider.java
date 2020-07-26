package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.DataLoadingState;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.TrafficDataLoader;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender.TrafficBitmap;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class TrafficTileProvider implements TileProvider {
    public static final int MAX_ZOOM_RENDER = 16;

    private TrafficBitmap trafficBitmap;
    private TrafficDataLoader trafficDataLoader;

    public TrafficTileProvider(Context context) {
        trafficBitmap = new TrafficBitmap();
        trafficDataLoader = TrafficDataLoader.getInstance(context);
    }

    public void setDataLoadingState(DataLoadingState dataLoadingState) {
        trafficDataLoader.setDataLoadingState(dataLoadingState);
    }

    @Override
    public Tile getTile(int x, int y, int z) {
        if (z < 15 || z > MAX_ZOOM_RENDER) return NO_TILE;
        try {
            TileCoordinates renderTile = TileCoordinates.getTileCoordinates(x, y, z);
            trafficDataLoader.loadTrafficDataFromServerAsync(renderTile, true);
            return generateTile(renderTile);
        } catch (Exception e) {
        }
        return null;
    }

    private Tile generateTile(TileCoordinates renderTile) {
        int scale = getScaleByZoom(renderTile);
        List<StatusRenderDataEntity> dataList = trafficDataLoader.loadDataFromLocal(renderTile);
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, dataList, scale, null);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
        }
        return null;
    }

    private int getScaleByZoom(TileCoordinates tile) {
        switch (tile.z) {
            case 15:
                return 2;
            default:
                return 2;
        }
    }
}
