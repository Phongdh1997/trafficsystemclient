package com.hcmut.admin.bktrafficsystem.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

public class LocationRequire implements LocationListener {
    private Context context;
    private Location getLastKnownLocation;
    private Integer[] velocities = new Integer[20];

    public LocationRequire(Context context) {
        this.context = context;
    }

    public void start() {
        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (enabledGPS) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
            getLastKnownLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (getLastKnownLocation == null) {
                getLastKnownLocation = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        int velocity = (int) (getLastKnownLocation.distanceTo(location)*3.6/5); // convert km/h
        LocationUtil.setLastVelocity(velocity, velocities);
        int avgVelocity = LocationUtil.getAvgVelocity(velocities);
        mListener.onUpdate(new LatLng(location.getLatitude(), location.getLongitude()), avgVelocity);
        getLastKnownLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private LocationUpdateListener mListener;

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.mListener = listener;
    }

    public interface LocationUpdateListener {
        void onUpdate(LatLng originPos, int avgVelocity);
    }
}
