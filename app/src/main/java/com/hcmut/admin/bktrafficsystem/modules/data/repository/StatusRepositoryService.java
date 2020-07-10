package com.hcmut.admin.bktrafficsystem.modules.data.repository;

import com.hcmut.admin.bktrafficsystem.modules.data.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

public interface StatusRepositoryService {
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom);
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters);
}
