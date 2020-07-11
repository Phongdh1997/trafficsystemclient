package com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PolylineTileRenderHandlerImpl extends TileRenderHandler {
    private WeakReference<GoogleMap> googleMapWeakReference;

    public PolylineTileRenderHandlerImpl(GoogleMap googleMap) {
        super();
        googleMapWeakReference = new WeakReference<>(googleMap);
    }

    @Override
    public <T> Bitmap render(final TileCoordinates tile, List<T> datas) {
        final List<PolylineOptions> polylineOptionsList = new ArrayList<>();
        if (polylineOptionsList != null && polylineOptionsList.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GoogleMap googleMap = googleMapWeakReference.get();
                    if (googleMap != null) {
                        for (PolylineOptions polylineOptions : polylineOptionsList) {
                            googleMap.addPolyline(polylineOptions);
                        }
                        tileLoaded(tile);
                    }
                }
            });
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
