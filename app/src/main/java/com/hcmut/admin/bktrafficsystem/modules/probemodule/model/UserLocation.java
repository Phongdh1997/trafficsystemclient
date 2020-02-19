package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import afu.org.checkerframework.checker.nullness.qual.NonNull;

public class UserLocation {

    private int id;
    private double longitude;
    private double latitude;
    private Date timestamp;

    public UserLocation(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = Calendar.getInstance().getTime();
        id = -1;
    }

    public UserLocation(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.timestamp = Calendar.getInstance().getTime();
        id = -1;
    }

    public UserLocation(int id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public UserLocation() {
        id = -1;
        longitude = 0.0;
        latitude = 0.0;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return formatter.format(timestamp);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "No" + id + ": Long: " + longitude + " - Lati: " + latitude + " - Speed = " + timestamp;
    }
}
