package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

public interface RoomDatabaseService {
    void insertTrafficStatus(List<StatusRenderData> datas);
    List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds);
    List<StatusRenderDataEntity> getTrafficStatus();
    void deleteAll();
}
