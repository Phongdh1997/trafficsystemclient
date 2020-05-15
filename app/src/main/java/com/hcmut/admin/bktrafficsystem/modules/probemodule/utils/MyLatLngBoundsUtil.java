package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MyLatLngBoundsUtil {
    public static LatLngBounds tileToLatLngBound(final int x, final int y, final int zoom) {
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);
        LatLng southwest = new LatLng(south, west);
        LatLng northeast = new LatLng(north, east);
        Log.e("cal", "" + north + ", " + south + ", " + west + ", " + east);
        return new LatLngBounds(southwest, northeast);
    }

    private static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
}
