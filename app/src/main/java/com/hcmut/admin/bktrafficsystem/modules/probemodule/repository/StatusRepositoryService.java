package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public interface StatusRepositoryService {
    void loadStatusRenderData(UserLocation userLocation, double zoom);
    void setStatusOverlayRender(StatusOverlayRender statusOverlayRender);
}
