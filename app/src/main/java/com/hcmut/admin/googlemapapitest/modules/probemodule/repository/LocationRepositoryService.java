package com.hcmut.admin.googlemapapitest.modules.probemodule.repository;

import com.hcmut.admin.googlemapapitest.modules.probemodule.model.UserLocation;

public interface LocationRepositoryService {
    void postLocationRecord(UserLocation prevUserLocation, UserLocation currUserLocation);
}
