package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public class MovingDetection {
    private static final double MOVING_DISTANCE_LIMIT_UNIT = 0.9;

    private UserLocation prevLocation;
    private UserLocation currLocation;

    public void setCurrLocation(UserLocation currLocation) {
        this.currLocation = currLocation;
    }

    /**
     *
     * @return true if distance between two location greater than limited distance, else return false
     */
    public boolean isMoving() {
        if (prevLocation == null || currLocation == null) {
            prevLocation = currLocation;
            return true;
        }
        double realDistance = caculateDistance();
        double limitDistance = meansureDistanceLimit();
        prevLocation = currLocation;
        Log.e("Distance", "Real: " + realDistance + ", limit: " + limitDistance);
        return realDistance > limitDistance;
    }

    /**
     * Calculate limited Distance for any time frame for two location
     * @return
     */
    private double meansureDistanceLimit() {
        double timeFrame = DateConverter.caculateTimeFrame(
                prevLocation.getTimestamp(), currLocation.getTimestamp());
        return timeFrame * MOVING_DISTANCE_LIMIT_UNIT;
    }

    /*
     * Haversine formula: a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2) c = 2 ⋅
     * atan2( √a, √(1−a) ) d = R ⋅ c where φ is latitude, λ is longitude, R is
     * earth’s radius (mean radius = 6,371km); note that angles need to be in
     * radians to pass to trig functions!
     * @return: distance (m)
     */
    private double caculateDistance() {
        double distance = 0.0;
        double R = 6371e3; // metres
        double phi1 = Math.toRadians(prevLocation.getLatitude());
        double phi2 = Math.toRadians(currLocation.getLatitude());
        double deltaPhi = Math.toRadians(currLocation.getLatitude() - prevLocation.getLatitude());
        double deltaLamda = Math.toRadians(currLocation.getLongitude() - prevLocation.getLongitude());
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLamda / 2) * Math.sin(deltaLamda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        distance = R * c;
        return distance;
    }
}
