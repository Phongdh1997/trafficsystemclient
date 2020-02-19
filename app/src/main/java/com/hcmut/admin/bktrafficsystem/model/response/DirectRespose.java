package com.hcmut.admin.bktrafficsystem.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectRespose {
    @SerializedName("_id")
    private String pathId;
    @SerializedName("distance")
    private int distance;
    @SerializedName("coords")
    private List<Coord> coords;

    public String getPathId() {
        return pathId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void setCoords(List<Coord> coords) {
        this.coords = coords;
    }
}
