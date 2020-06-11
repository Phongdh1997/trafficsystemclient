package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.GoogleMapMemoryManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

public abstract class StatusRender {
    public static final int TILE_ZOOM_LEVEL = 15;
    private TileCoordinates lastCenterTile;
    private GoogleMapMemoryManager mapMemoryManager;
    private final int NEAR_LEVEL_TO_LOAD;

    public StatusRender (SupportMapFragment mapFragment) {
        mapMemoryManager = GoogleMapMemoryManager.getInstance(mapFragment);
        NEAR_LEVEL_TO_LOAD = GroundOverlayMatrix.MATRIX_WIDTH / 2;
    }

    public void onCameraMoving(GoogleMap googleMap){
        float zoom = googleMap.getCameraPosition().zoom;
        if (zoom < 15f || zoom > 20) {
            return;
        }
        mapMemoryManager.onMapMove(zoom);
        final LatLng centerPoint = googleMap.getCameraPosition().target;
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                TileCoordinates currentTile = getCenterRenderTile(centerPoint);
                if (currentTile != null) {
                    handleCameraMoving(currentTile);
                }
            }
        });
    }

    private TileCoordinates getCenterRenderTile (LatLng centerPoint) {
        try {
            TileCoordinates currentTile = MyLatLngBoundsUtil.getTileNumber(
                    centerPoint.latitude,
                    centerPoint.longitude,
                    TILE_ZOOM_LEVEL);
            if (lastCenterTile == null) {
                lastCenterTile = currentTile;
                return currentTile;
            }
            Log.e("level", "" + currentTile.getNearLevel(lastCenterTile));
            if (currentTile.getNearLevel(lastCenterTile) > NEAR_LEVEL_TO_LOAD - 1) {  // move to other tile
                Log.e("move", "move to other tile");
                lastCenterTile = currentTile;
                return currentTile;
            } else {
                Log.e("move", "is old tile");
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return null;
    }

    protected abstract void handleCameraMoving(TileCoordinates currentTile);

    public abstract void refreshRenderStatus(TileCoordinates centerTile);
}
