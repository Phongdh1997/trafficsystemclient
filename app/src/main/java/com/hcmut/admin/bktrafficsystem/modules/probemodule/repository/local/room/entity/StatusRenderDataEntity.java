package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

@Entity(tableName = "status_render_data")
public class StatusRenderDataEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "segment")
    public Long segment;

    @ColumnInfo(name = "color")
    public String color;

    @ColumnInfo(name = "startLat")
    public double startLat;

    @ColumnInfo(name = "startLng")
    public double startLng;

    @ColumnInfo(name = "endLat")
    public double endLat;

    @ColumnInfo(name = "endLng")
    public double endLng;

    public LatLng getStartLatlng () {
        return new LatLng(startLat, startLng);
    }

    public LatLng getEndLatlng () {
        return new LatLng(endLat, endLng);
    }

    public StatusRenderDataEntity() {

    }

    public StatusRenderDataEntity(StatusRenderData data) {
        segment = data.getSegment();
        color = data.getColor();
        try {
            List<List<Double>> coord = data.getPolyline().getCoordinates();
            startLat = coord.get(0).get(1);
            startLng = coord.get(0).get(0);
            endLat = coord.get(1).get(1);
            endLng = coord.get(1).get(0);
        } catch (Exception e) {

        }
    }
}
