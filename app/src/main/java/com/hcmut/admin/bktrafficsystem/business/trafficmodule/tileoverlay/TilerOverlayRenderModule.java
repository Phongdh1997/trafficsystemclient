package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.DataLoadingState;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.TrafficDataLoader;

public class TilerOverlayRenderModule {
    private TileOverlay statusTileOverlay;
    private TrafficTileProvider trafficTileProvider;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public TilerOverlayRenderModule(GoogleMap map, Context context) {
        trafficTileProvider = new TrafficTileProvider(context, new TrafficDataLoader.ClearCacheCallback() {
            @Override
            public void onClearCache() {
                notifyDataChange();
            }
        });
        statusTileOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(trafficTileProvider));
    }

    /**
     * clear Tile Cache, current data source will be displayed
     */
    public synchronized void notifyDataChange() {
        clearTileCache();
    }

    private void clearTileCache() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                statusTileOverlay.clearTileCache();
            }
        });
    }

    public void setTrafficEnable(boolean isEnable) {
        statusTileOverlay.setVisible(isEnable);
    }
}
