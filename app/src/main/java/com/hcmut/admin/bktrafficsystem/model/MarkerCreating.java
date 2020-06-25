package com.hcmut.admin.bktrafficsystem.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerCreating {
    private LatLng latLng;
    private Marker marker;

    public MarkerCreating (LatLng latLng) {
        this.latLng = latLng;
    }

    public void createMarker(Context context, GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation) {
        if (googleMap == null) return;
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptor));

        if (isMoveToCurrentLocation) {
            // move camera to current location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    public void removeMarker() {
        if (marker != null) {
            marker.remove();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int bgDrawableId) {
        Drawable background = ContextCompat.getDrawable(context, bgDrawableId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
