package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class BitmapTileRenderHandlerImpl extends TileRenderHandler {

    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficBitmap trafficBitmap;
    private TileOverlayPool tileOverlayPool;

    public BitmapTileRenderHandlerImpl (GoogleMap googleMap, HashMap<TileCoordinates, String> tileStates) {
        super(tileStates);
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficBitmap = new TrafficBitmap();
        tileOverlayPool = new TileOverlayPool(super.tileStates);
    }

    @Override
    public Bitmap render(TileCoordinates tile, List<StatusRenderData> datas, HashMap<TileCoordinates, String> tileStates) {
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(tile, StatusRenderData.parseBitmapLineData(datas));
        if (bitmap != null) {
            invalidate(tile, bitmap);
        }
        return bitmap;
    }

    @Override
    public void render(TileCoordinates tile, Bitmap bitmap, HashMap<TileCoordinates, String> tileStates) {
        if (tile != null && bitmap != null) {
            invalidate(tile, bitmap);
        }
    }

    /**
     *
     * @param tileCoordinates
     * @param bitmap
     */
    private void invalidate(TileCoordinates tileCoordinates, @Nullable Bitmap bitmap) {
        if (bitmap != null) {
            performRenderBitmapToTile(tileCoordinates, bitmap);
        }
    }

    /**
     * Trigger to refresh tile with available bitmap
     * tile is rendered when it is in matrix
     */
    private void performRenderBitmapToTile(final TileCoordinates target, @NotNull final Bitmap bitmap) {
        final GoogleMap googleMap = googleMapWeakReference.get();
        if (googleMap != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TileCoordinates centerTile = TileCoordinates.getCenterTile(googleMap);
                    if (centerTile != null && target.isInsideOfMatrix(centerTile)) {
                        GroundOverlay groundOverlay = tileOverlayPool.poll(centerTile);
                        if (groundOverlay != null) {
                            groundOverlay.setImage(BitmapDescriptorFactory.fromBitmap(bitmap));
                            groundOverlay.setPositionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
                        } else {
                            GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
                            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
                            groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);

                        }
                        setTileState(target, GroundOverlayMatrix.LOADED_OVERLAY);
                        tileOverlayPool.recycle(target, groundOverlay);
                    } else {
                        setTileState(target, GroundOverlayMatrix.LOAD_FAIL_OVERLAY);
                    }
                }
            });
        }
    }
}
