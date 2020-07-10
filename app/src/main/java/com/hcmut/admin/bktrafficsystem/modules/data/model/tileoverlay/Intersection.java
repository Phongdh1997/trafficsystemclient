package com.hcmut.admin.bktrafficsystem.modules.data.model.tileoverlay;

import com.google.android.gms.maps.model.LatLng;

public class Intersection {
    public static LatLng findIntersection(LatLng s1, LatLng e1, LatLng s2, LatLng e2) {
        double a1 = e1.longitude - s1.longitude;
        double b1 = s1.latitude - e1.latitude;
        double c1 = a1 * s1.latitude + b1 * s1.longitude;

        double a2 = e2.longitude - s2.longitude;
        double b2 = s2.latitude - e2.latitude;
        double c2 = a2 * s2.latitude + b2 * s2.longitude;

        double delta = a1 * b2 - a2 * b1;
        return new LatLng((b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta);
    }

    public static boolean isAmong (LatLng point, LatLng start, LatLng end) {
        double d1 = Math.sqrt(Math.pow(point.latitude - start.latitude, 2) +
                Math.pow(point.longitude - start.longitude, 2));
        double d2 = Math.sqrt(Math.pow(point.latitude - end.latitude, 2) +
                Math.pow(point.longitude - end.longitude, 2));
        double d3 = Math.sqrt(Math.pow(end.latitude - start.latitude, 2) +
                Math.pow(end.longitude - start.longitude, 2));
        return !((d1 + d2) > d3);
    }
}
