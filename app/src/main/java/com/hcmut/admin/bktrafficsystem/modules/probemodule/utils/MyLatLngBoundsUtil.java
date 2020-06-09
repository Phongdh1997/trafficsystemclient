package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;

public class MyLatLngBoundsUtil {
    public static LatLngBounds tileToLatLngBound(TileCoordinates tileCoordinates) {
        final int x = tileCoordinates.x;
        final int y = tileCoordinates.y;
        final int zoom = tileCoordinates.z;
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);
        LatLng southwest = new LatLng(south, west);
        LatLng northeast = new LatLng(north, east);
        return new LatLngBounds(southwest, northeast);
    }

    private static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     *
     * @param lat
     * @param lon
     * @param zoom: zoom of tile which want to get
     * @return: tile with zoom level contain latlng
     */
    public static TileCoordinates getTileNumber(final double lat, final double lon, final int zoom) throws TileCoordinates.TileCoordinatesNotValid {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return TileCoordinates.getTileCoordinates(xtile, ytile, zoom);
    }

    public static TileCoordinates convertTile(TileCoordinates source, int zoom) {
        LatLng center = MyLatLngBoundsUtil.tileToLatLngBound(source).getCenter();
        try {
            return MyLatLngBoundsUtil.getTileNumber(center.latitude, center.longitude, zoom);
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
        }
        return null;
    }
}
