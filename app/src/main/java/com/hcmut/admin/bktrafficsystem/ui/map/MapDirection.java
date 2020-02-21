package com.hcmut.admin.bktrafficsystem.ui.map;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business.MarkerCreating;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapDirection {
    private MarkerCreating startMarker;
    private MarkerCreating endMarker;

    private Polyline directPolyline;

    public void clearDirection () {
        if (startMarker != null) {
            startMarker.removeMarker();
        }
        if (endMarker != null) {
            endMarker.removeMarker();
        }
        if (directPolyline != null) {
            directPolyline.remove();
        }
    }

    public void drawDirectPolylines (@NotNull PolylineOptions polylineOptions, GoogleMap mMap, Context context) {
        clearDirection();
        List<LatLng> points = polylineOptions.getPoints();
        if (points == null) return;
        startMarker = new MarkerCreating(new UserLocation(points.get(0)));
        endMarker = new MarkerCreating(new UserLocation(points.get(points.size() - 1)));
        startMarker.createMarker(context, mMap, R.drawable.ic_start_position, false);
        endMarker.createMarker(context, mMap, R.drawable.ic_end_position, false);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(points.get(0));
        builder.include(points.get(points.size() - 1));
        LatLngBounds bounds = builder.build();
        int padding = 200;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        directPolyline = mMap.addPolyline(polylineOptions);
    }
}
