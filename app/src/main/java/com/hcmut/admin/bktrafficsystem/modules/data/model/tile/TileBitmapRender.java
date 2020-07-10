package com.hcmut.admin.bktrafficsystem.modules.data.model.tile;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.LatLngBounds;

@Deprecated
public class TileBitmapRender {
    private Bitmap bitmap;
    private LatLngBounds latLngBounds;

    public TileBitmapRender(Bitmap bitmap, LatLngBounds latLngBounds) {
        this.bitmap = bitmap;
        this.latLngBounds = latLngBounds;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void recycleBitmap () {
        try {
            bitmap.recycle();
        } catch (Exception e) {}
    }
}
