package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.gson.internal.$Gson$Types;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

public class GroundOverlayMatrixItem {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String INIT_OVERLAY = "INIT_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    private GroundOverlay groundOverlay;
    private String state;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public GroundOverlayMatrixItem () {
        state = INIT_OVERLAY;
    }

    public void invalidate (@NotNull Bitmap bitmap, @NotNull TileCoordinates tileCoordinates, @NotNull final GoogleMap googleMap) {
        if (!state.equals(LOADED_OVERLAY)) {
            final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    state = LOADED_OVERLAY;
                    groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);
                }
            });
        }
    }

    public void invalidate (@NotNull final Bitmap bitmap, @NotNull final TileCoordinates tileCoordinates, @NotNull final GroundOverlayMatrixItem idleMatrixItem) {
        if (!state.equals(LOADED_OVERLAY)) {
            final GroundOverlay idleOverlay = idleMatrixItem.getGroundOverlay();
            final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    idleOverlay.setImage(bitmapDescriptor);
                    idleOverlay.setPositionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates));
                    groundOverlay = idleOverlay;
                    state = LOADED_OVERLAY;
                }
            });
        }
    }

    public GroundOverlay getGroundOverlay() {
        return groundOverlay;
    }

    public void setGroundOverlay(GroundOverlay groundOverlay) {
        this.groundOverlay = groundOverlay;
    }

    public boolean isOvelayLoaded() {
        return groundOverlay != null;
    }

    public boolean isOverlayInit() {
        return state.equals(INIT_OVERLAY);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}