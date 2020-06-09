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

public class MatrixStatusRenderImpl extends StatusRender {
    private GroundOverlayMatrix groundOverlayMatrix;

    public MatrixStatusRenderImpl(GoogleMap googleMap, Context context, SupportMapFragment mapFragment) {
        super(mapFragment);
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
