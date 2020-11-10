package com.hcmut.admin.bktrafficsystem.repository;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;

import java.util.List;

public interface RoomDatabaseService {
    void insertTrafficStatus(List<StatusRenderDataEntity> datas);
    List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds, String streetType);
    List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds);
    List<StatusRenderDataEntity> getTrafficStatus();
    void deleteAll();
}
