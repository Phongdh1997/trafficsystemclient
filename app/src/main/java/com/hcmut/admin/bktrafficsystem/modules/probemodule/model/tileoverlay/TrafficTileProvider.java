package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.TrafficBitmap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.TrafficBitmap.TILE_ZOOM_15_SCALE;

public class TrafficTileProvider implements TileProvider {
    public static final int MAX_ZOOM_RENDER = 17;

    private Context context;
    private TrafficBitmap trafficBitmap;
    private TrafficDataLoader trafficDataLoader;

    public TrafficTileProvider(Context context) {
        this.context = context;
        trafficBitmap = new TrafficBitmap();
        trafficDataLoader = new TrafficDataLoader(context);
    }

    public void notifyDataChange () {
        trafficDataLoader.notifyDataChange();
    }

    @Override
    public Tile getTile(int x, int y, int z) {
        if (z < 15 || z > MAX_ZOOM_RENDER) return NO_TILE;

        try {
            TileCoordinates renderTile = TileCoordinates.getTileCoordinates(x, y, z);
            List<StatusRenderDataEntity> dataList = trafficDataLoader.loadTrafficData(renderTile);
            int scale = TILE_ZOOM_15_SCALE;
            Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, dataList, scale);
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
            }
        } catch (Exception e) {
        }
        return null;
    }
}
