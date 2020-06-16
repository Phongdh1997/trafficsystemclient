package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

import java.util.List;

public interface RoomDatabaseService {
    void insertTrafficStatus(List<StatusRenderDataEntity> datas);
    List<StatusRenderDataEntity> getTrafficStatus(TileCoordinates tile);
    List<StatusRenderDataEntity> getTrafficStatus();
    void deleteAll();
}
