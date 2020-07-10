package com.hcmut.admin.bktrafficsystem.business.tileoverlay;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class TilerOverlayRender {
    private GoogleMap map;

    private TileOverlay statusTileOverlay;
    private TrafficTileProvider trafficTileProvider;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public TilerOverlayRender(GoogleMap map, Context context) {
        this.map = map;
        trafficTileProvider = new TrafficTileProvider(context);
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(trafficTileProvider));
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public synchronized void notifyDataChange() {
        Log.e("test", "refresh");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                statusTileOverlay.clearTileCache();
            }
        });
        trafficTileProvider.notifyDataChange();
    }
}
