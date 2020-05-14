package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StatusRenderData {
    @SerializedName("segment")
    @Expose
    private Long segment;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;

    @NonNull
    @Override
    public String toString() {
        return "polyline: " + polyline.toString() + ", color: " + color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public List<LatLng> getLatLngPolyline() {
        if (polyline != null) {
            return polyline.getLatLngPolyline();
        }
        return null;
    }

    public static class Polyline {

        @SerializedName("coordinates")
        @Expose
        private List<List<Double>> coordinates = null;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }

        public List<LatLng> getLatLngPolyline() {
            try {
                List<LatLng> l = new ArrayList<>();
                l.add(new LatLng(coordinates.get(0).get(1), coordinates.get(0).get(0)));
                l.add(new LatLng(coordinates.get(1).get(1), coordinates.get(1).get(0)));
                return l;
            } catch (Exception e) {}
            return null;
        }

        @NonNull
        @Override
        public String toString() {
            try {
                String s = "";
                for (List<Double> coords : coordinates) {
                    s += "{ " + coords.get(0) + ", " + coords.get(1) + "} ";
                }
                return s;
            } catch (Exception e) {
            }
            return "";
        }
    }

}

