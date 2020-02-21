package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public class MarkerCreating {

    private UserLocation userLocation;
    private Marker marker;

    public MarkerCreating (UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public void createMarker(Context context, GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation) {
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                .icon(bitmapDescriptor));

        if (isMoveToCurrentLocation) {
            // move camera to current location
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
