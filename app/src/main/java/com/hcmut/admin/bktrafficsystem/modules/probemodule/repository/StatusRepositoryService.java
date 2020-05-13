package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

import java.lang.ref.WeakReference;
import java.util.List;

public interface StatusRepositoryService {
    void loadStatusRenderData(UserLocation userLocation, double zoom, WeakReference<GoogleMap> mapRef);
    LiveData<GeoJsonLayer> getStatusRenderData();
}
