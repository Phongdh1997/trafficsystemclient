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
    @SerializedName("velocity")
    @Expose
    private Double velocity;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("segment")
    @Expose
    private Long segment;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("priority")
    @Expose
    private Long priority;
    @SerializedName("_class")
    @Expose
    private String _class;

    public static List<PolylineOptions> parsePolylineOptions(List<StatusRenderData> trafficStatusDatas) {
        if (trafficStatusDatas == null) return new ArrayList<>();
        double startLat;
        double startLng;
        double endLat;
        double endLng;
        List<PolylineOptions> polylineOptionsList = new ArrayList<>();
        PolylineOptions polylineOptions;
        List<List<Double>> coordinates;
        for (StatusRenderData trafficData : trafficStatusDatas) {
            try {
                coordinates = trafficData.getPolyline().getCoordinates();
                startLat = coordinates.get(0).get(1);
                startLng = coordinates.get(0).get(0);
                endLat = coordinates.get(1).get(1);
                endLng = coordinates.get(1).get(0);
                polylineOptions = new PolylineOptions()
                        .width(7).color(Color.parseColor(trafficData.getColor()))
                        .add(new LatLng(startLat, startLng), new LatLng(endLat, endLng));
                polylineOptionsList.add(polylineOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Status data", trafficData.toString());
        }
        return polylineOptionsList;
    }

    @NonNull
    @Override
    public String toString() {
        return "segment: " + segment + ", speed: " + velocity + ", polyline: " + polyline.toString();
    }

    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSegment() {
        return segment;
    }

    public void setSegment(Long segment) {
        this.segment = segment;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public class Polyline {

        @SerializedName("coordinates")
        @Expose
        private List<List<Double>> coordinates = null;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("type")
        @Expose
        private String type;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @NonNull
        @Override
        public String toString() {
            String s = "";
            for (List<Double> coords : coordinates) {
                s += "{ " + coords.get(0) + ", " + coords.get(1) + "} ";
            }
            return s;
        }
    }

}

