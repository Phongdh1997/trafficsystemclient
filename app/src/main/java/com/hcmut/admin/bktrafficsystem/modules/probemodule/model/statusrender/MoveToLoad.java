package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

public class MoveToLoad {
    public static final int TILE_ZOOM_LEVEL = 15;
    private TileCoordinates lastCenterTile;
    private GroundOverlayMatrix groundOverlayMatrix;

    public MoveToLoad (GoogleMap googleMap) {
        groundOverlayMatrix = new GroundOverlayMatrix(googleMap);
    }

    public void cameraMove(GoogleMap googleMap) {
        float zoom = googleMap.getCameraPosition().zoom;
        if (zoom < 15f || zoom > 20) {
            return;
        }
        LatLng centerPoint = googleMap.getCameraPosition().target;
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
}
