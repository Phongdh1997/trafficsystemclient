package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile;

import java.lang.ref.WeakReference;
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

public class CustomTileProvider implements TileProvider {

    private TrafficTileLoader trafficTileLoader;
    private TileBitmap tileBitmapHelper = new TileBitmap();
    private MutableLiveData<TileBitmapRender> tileBitmapLiveData = new MutableLiveData<>();

    public CustomTileProvider(Context context) {
        trafficTileLoader = new TrafficTileLoader(context);
    }

    public LiveData<TileBitmapRender> getTileBitmapLiveData () {
        return tileBitmapLiveData;
    }

    public void clearTileDataCached() {
        trafficTileLoader.clearTileDataCached();

    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        if (zoom < 15 || zoom > 21) {
            return NO_TILE;
        }
        try {
            TileCoordinates tileCoordinates = TileCoordinates.getTileCoordinates(x, y, zoom);
            List<StatusRenderDataEntity> statusDatas = trafficTileLoader.loadData(tileCoordinates);
            Bitmap bitmap = tileBitmapHelper.getTileWithScale(x, y, zoom, statusDatas);
            if (bitmap != null) {
                LatLngBounds latLngBounds = MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates);
                tileBitmapLiveData.postValue(new TileBitmapRender(bitmap, latLngBounds));
                return NO_TILE;
            }
        } catch (Exception e) {}
        return null;
    }
}