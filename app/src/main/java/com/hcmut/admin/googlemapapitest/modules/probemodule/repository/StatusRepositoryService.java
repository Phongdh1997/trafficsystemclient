package com.hcmut.admin.googlemapapitest.modules.probemodule.repository;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.googlemapapitest.modules.probemodule.model.UserLocation;

import java.util.List;

public interface StatusRepositoryService {
    void loadStatusRenderData(UserLocation userLocation, double zoom);
    LiveData<List<PolylineOptions>> getStatusRenderData();
}
