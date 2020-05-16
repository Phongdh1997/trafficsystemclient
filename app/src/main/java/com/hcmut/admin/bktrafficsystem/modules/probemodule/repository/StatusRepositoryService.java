package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

public interface StatusRepositoryService {
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom);
}
