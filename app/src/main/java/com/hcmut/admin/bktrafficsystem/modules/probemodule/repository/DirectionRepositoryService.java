package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public interface DirectionRepositoryService {
    void loadDirectionRenderData(UserLocation startPoint, UserLocation endPoint);
    LiveData<PolylineOptions> getDirectionRenderData();
}
