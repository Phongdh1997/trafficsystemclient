package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficBitmap trafficBitmap;

    public BitmapTileRenderHandlerImpl (GoogleMap googleMap, HashMap<TileCoordinates, String> tileStates) {
        super(tileStates);
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficBitmap = new TrafficBitmap();
    }

    @Override
    public void render(TileCoordinates tile, List<StatusRenderData> datas, HashMap<TileCoordinates, String> tileStates) {
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(tile, StatusRenderData.parseBitmapLineData(datas));
        if (bitmap != null) {
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
    private void performRenderBitmapToTile(final TileCoordinates target, @NotNull Bitmap bitmap) {
        final GoogleMap googleMap = googleMapWeakReference.get();
        if (googleMap != null) {
            final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    googleMap.addGroundOverlay(groundOverlayOptions);
                    setTileState(target, GroundOverlayMatrix.LOADED_OVERLAY);
                }
            });
        }
        try {
            bitmap.recycle();
        } catch (Exception e) {
        }
    }
}
