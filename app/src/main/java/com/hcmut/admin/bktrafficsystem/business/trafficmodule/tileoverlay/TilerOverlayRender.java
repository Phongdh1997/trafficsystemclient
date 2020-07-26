package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.DataLoadingState;

public class TilerOverlayRender {
    private TileOverlay statusTileOverlay;
    private TrafficTileProvider trafficTileProvider;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private DataLoadingState dataLoadingState;

    public TilerOverlayRender(GoogleMap map, Context context) {
        trafficTileProvider = new TrafficTileProvider(context);
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(trafficTileProvider));

        dataLoadingState = new DataLoadingState(new DataLoadingState.TileOverlayClearCallback() {
            @Override
            public void onClearTileCache() {
                clearTileCache();
            }
        });
        trafficTileProvider.setDataLoadingState(dataLoadingState);
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public synchronized void notifyDataChange() {
        Log.e("test", "refresh");
        clearTileCache();
        dataLoadingState.clear();
    }

    private void clearTileCache() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                statusTileOverlay.clearTileCache();
            }
        });
    }
}
