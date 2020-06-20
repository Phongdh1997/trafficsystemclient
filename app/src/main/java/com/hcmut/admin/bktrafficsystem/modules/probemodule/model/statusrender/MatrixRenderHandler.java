package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.TrafficBitmap.TILE_ZOOM_15_SCALE;

public class MatrixRenderHandler extends TileRenderHandler {
    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficBitmap trafficBitmap;

    public MatrixRenderHandler (GoogleMap googleMap) {
        super();
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficBitmap = new TrafficBitmap();
    }

    @Override
    public <T> Bitmap render(TileCoordinates tile, List<T> datas) {
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(tile, datas, 8, 0.00018f);
        if (bitmap != null) {
            synchronized (MatrixRenderHandler.class) {
                GroundOverlayMatrixItem groundOverlayMatrixItem = GroundOverlayMatrix.getMatrixItem(tile);
                GoogleMap googleMap = googleMapWeakReference.get();
                if (groundOverlayMatrixItem != null && googleMap != null) {
                    groundOverlayMatrixItem.invalidateItself(bitmap, tile, googleMap);
                }
            }
        }
        return null;
    }

    @Override
    public void render(TileCoordinates tile, Bitmap bitmap) {

    }

    @Override
    public void clearRender() {

    }
}
