package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public interface LocationRepositoryService {
    void postLocationRecord(UserLocation prevUserLocation, UserLocation currUserLocation);
}
