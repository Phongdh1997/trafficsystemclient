package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class PolylineTileRenderHandlerImpl extends TileRenderHandler {
    private WeakReference<GoogleMap> googleMapWeakReference;

    public PolylineTileRenderHandlerImpl(GoogleMap googleMap, HashMap<TileCoordinates, String> tileStates) {
        super(tileStates);
        googleMapWeakReference = new WeakReference<>(googleMap);
    }

    @Override
    public Bitmap render(final TileCoordinates tile, List<StatusRenderData> datas, HashMap<TileCoordinates, String> tileStates) {
        final List<PolylineOptions> polylineOptionsList = StatusRenderData.parsePolylineOption(datas);
        if (polylineOptionsList != null && polylineOptionsList.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GoogleMap googleMap = googleMapWeakReference.get();
                    if (googleMap != null) {
                        for (PolylineOptions polylineOptions : polylineOptionsList) {
                            googleMap.addPolyline(polylineOptions);
                        }
                        setTileState(tile, GroundOverlayMatrix.LOADED_OVERLAY);
                    }
                }
            });
        }
        return null;
    }

    @Override
    public void render(TileCoordinates tile, Bitmap bitmap, HashMap<TileCoordinates, String> tileStates) {

    }
}
