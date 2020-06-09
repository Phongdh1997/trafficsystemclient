package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapMemoryManager {
    private SupportMapFragment mapFragment;

    private int moveCount = 0;
    private int lastZoom = 5;

    public GoogleMapMemoryManager(SupportMapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void onMapMove (float zoom) {
        int currentZoom = (int) zoom;
        Log.e("test", "zoom: " + currentZoom);
        if (currentZoom != lastZoom) {
            lastZoom = currentZoom;
            moveCount = 0;
            mapFragment.onLowMemory();
            Log.e("test", "lowMemory");
        } else {
            if (moveCount > getLimitMoveCount(currentZoom)) {
                moveCount = 0;
                mapFragment.onLowMemory();
                Log.e("test", "lowMemory");
            } else {
                moveCount++;
            }
        }
        Log.e("test", "count: " + moveCount);
    }

    public int getLimitMoveCount (int zoom) {
        switch (zoom) {
            case 15:
                return 60;
            case 16:
                return 40;
            case 17:
                return 20;
            case 18:
                return 16;
            case 19:
                return 14;
            case 20:
                return 12;
            case 21:
                return 10;
        }
        return 200;
    }
}
