package com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nullable;

import static com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender.TrafficBitmap.TILE_ZOOM_15_SCALE;

public class BitmapTileRenderHandlerImpl extends TileRenderHandler {

    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficBitmap trafficBitmap;

    public BitmapTileRenderHandlerImpl (GoogleMap googleMap) {
        super();
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficBitmap = new TrafficBitmap();
    }

    @Override
    public <T> Bitmap render(TileCoordinates tile, List<T> datas) {
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(tile, datas, TILE_ZOOM_15_SCALE, null);
        if (bitmap != null) {
            invalidate(tile, bitmap);
        }
        return null;
    }

    @Override
    public void render(TileCoordinates tile, Bitmap bitmap) {
        if (tile != null && bitmap != null) {
            invalidate(tile, bitmap);
        }
    }

    @Override
    public void clearRender() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
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
                        GroundOverlay groundOverlay = null;
                        if (groundOverlay != null) {
                            groundOverlay.setImage(BitmapDescriptorFactory.fromBitmap(bitmap));
                            groundOverlay.setPositionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
                        } else {
                            GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
                            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
                            groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);

                        }
                        tileLoaded(target);
                    } else {
                        tileLoadFail(target);
                    }
                }
            });
        }
    }
}
