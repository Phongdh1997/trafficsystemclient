package com.hcmut.admin.bktrafficsystem.modules.data.repository;

import com.hcmut.admin.bktrafficsystem.modules.data.model.UserLocation;

public interface LocationRepositoryService {
    void postLocationRecord(UserLocation prevUserLocation, UserLocation currUserLocation);
}
