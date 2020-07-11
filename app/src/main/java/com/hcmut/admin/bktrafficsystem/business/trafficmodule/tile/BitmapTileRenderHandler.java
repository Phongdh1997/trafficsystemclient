package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tile;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import javax.annotation.Nullable;

@Deprecated
public class BitmapTileRenderHandler {
    private final int CACHE_LIMIT;
    private final int NUMBER_CLEARING;
    private boolean isClearing = false;

    public BitmapTileRenderHandler (int CACHE_LIMIT, int NUMBER_CLEARING) {
        this.CACHE_LIMIT = CACHE_LIMIT;
        this.NUMBER_CLEARING = NUMBER_CLEARING;
    }

    private Queue<GroundOverlay> groundOverlayQueue = new LinkedList<>();

    public void renderBitmapToMap(@Nullable TileBitmapRender tileBitmapRender, @Nullable GoogleMap googleMap) {
        if (tileBitmapRender == null || googleMap == null) return;

        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(tileBitmapRender.getBitmap()))
                .positionFromBounds(tileBitmapRender.getLatLngBounds());
        GroundOverlay groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);
        groundOverlayQueue.add(groundOverlay);

        // check to clear cache
        if (groundOverlayQueue.size() > CACHE_LIMIT && isClearing) {
            clearCache();
        }

        tileBitmapRender.recycleBitmap();
    }

    private void clearCache() {
        isClearing = true;
        for (int i = 0; i < NUMBER_CLEARING; i++) {
            Objects.requireNonNull(groundOverlayQueue.poll()).remove();
        }
        Log.e("clear", "clear cache");
        isClearing = false;
    }
}
