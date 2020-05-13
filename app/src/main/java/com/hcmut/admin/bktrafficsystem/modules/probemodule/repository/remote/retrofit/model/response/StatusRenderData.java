package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.hcmut.admin.bktrafficsystem.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatusRenderData {
//    @SerializedName("velocity")
//    @Expose
//    private Double velocity;
//    @SerializedName("_id")
//    @Expose
//    private String id;
//    @SerializedName("segment")
//    @Expose
//    private Long segment;
//    @SerializedName("source")
//    @Expose
//    private String source;
//    @SerializedName("createdAt")
//    @Expose
//    private String createdAt;
//    @SerializedName("updatedAt")
//    @Expose
//    private String updatedAt;
//    @SerializedName("priority")
//    @Expose
//    private Long priority;
//    @SerializedName("_class")
//    @Expose
//    private String _class;

    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;

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
        }
        Log.e("Status size", "size" + polylineOptionsList.size());
        return polylineOptionsList;
    }

    public static GeoJsonLayer getJsonLayerFromStatus (GoogleMap map, List<StatusRenderData> trafficStatusDatas) {
        //if (trafficStatusDatas == null || trafficStatusDatas.size() == 0) return null;
        GeoJsonLayer geoJsonLayer = new GeoJsonLayer(map, new JSONObject());

        for (int i = 0; i < 1000; i++) {
            ArrayList<LatLng> lineStringArray = new ArrayList<LatLng>();
            lineStringArray.add(new LatLng(10.765646, 106.659504));
            lineStringArray.add(new LatLng(10.780170, 106.655523));

            GeoJsonLineString lineString = new GeoJsonLineString(lineStringArray);
            GeoJsonFeature lineStringFeature = new GeoJsonFeature(lineString, null, null, null);

            GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
            lineStringStyle.setColor(Color.RED);

            lineStringFeature.setLineStringStyle(lineStringStyle);

            geoJsonLayer.addFeature(lineStringFeature);
        }
        Log.e("geojson", "finish");
        return geoJsonLayer;
    }

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

    public class Polyline {

        @SerializedName("coordinates")
        @Expose
        private List<List<Double>> coordinates = null;
//        @SerializedName("_id")
//        @Expose
//        private String id;
//        @SerializedName("type")
//        @Expose
//        private String type;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public ArrayList<LatLng> getLineStringArray() {
            ArrayList<LatLng> lineStringArray = new ArrayList<>();
            try {
                lineStringArray.add(new LatLng(coordinates.get(0).get(1), coordinates.get(0).get(0)));
                lineStringArray.add(new LatLng(coordinates.get(1).get(1), coordinates.get(1).get(0)));
            } catch (Exception e) {
            }
            return lineStringArray;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
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

