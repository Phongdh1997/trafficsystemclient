package com.hcmut.admin.bktrafficsystem.modules.data.model.statusrender;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.data.model.TileCoordinates;

public class MatrixStatusRenderImpl extends StatusRender {
    private GroundOverlayMatrix groundOverlayMatrix;

    public MatrixStatusRenderImpl(GoogleMap googleMap, Context context) {
        super();
        groundOverlayMatrix = new GroundOverlayMatrix(googleMap, context);
    }

    @Override
    protected void handleCameraMoving(TileCoordinates currentTile) {
        groundOverlayMatrix.renderMatrix(currentTile);
    }

    @Override
    public void refreshRenderStatus(TileCoordinates centerTile) {
        groundOverlayMatrix.refresh(centerTile);
    }
}
