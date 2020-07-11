package com.hcmut.admin.bktrafficsystem.repository;

import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.List;

public interface StatusRepositoryService {
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom);
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters);
}
