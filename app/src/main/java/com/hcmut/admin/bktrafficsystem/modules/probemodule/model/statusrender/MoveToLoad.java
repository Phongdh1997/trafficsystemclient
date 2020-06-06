package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.GoogleMapMemoryManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

public class MoveToLoad {
    public static final int TILE_ZOOM_LEVEL = 15;
    private TileCoordinates lastCenterTile;
    private GroundOverlayMatrix groundOverlayMatrix;
    private GoogleMapMemoryManager mapMemoryManager;

    public MoveToLoad (GoogleMap googleMap, Context context, SupportMapFragment mapFragment) {
        groundOverlayMatrix = new GroundOverlayMatrix(googleMap, context);
        mapMemoryManager = new GoogleMapMemoryManager(mapFragment);
    }

    public void cameraMove(GoogleMap googleMap) {
        float zoom = googleMap.getCameraPosition().zoom;
        if (zoom < 15f || zoom > 20) {
            return;
        }
        mapMemoryManager.onMapMove(zoom);
        final LatLng centerPoint = googleMap.getCameraPosition().target;
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TileCoordinates currentTile = MyLatLngBoundsUtil.getTileNumber(
                            centerPoint.latitude,
                            centerPoint.longitude,
                            TILE_ZOOM_LEVEL);
                    if (lastCenterTile == null) {
                        lastCenterTile = currentTile;
                        return;
                    }
                    if (!currentTile.equals(lastCenterTile)) {  // move to other tile
                        Log.e("move", "move to other tile");
                        lastCenterTile = currentTile;
                        groundOverlayMatrix.renderMatrix(currentTile);
                    } else {
                        Log.e("move", "is old tile");
                    }
                } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
                    tileCoordinatesNotValid.printStackTrace();
                }
            }
        });
    }
}
