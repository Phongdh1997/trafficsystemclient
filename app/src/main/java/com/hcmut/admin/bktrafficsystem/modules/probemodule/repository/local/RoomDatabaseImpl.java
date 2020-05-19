package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local;

import android.content.Context;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.TrafficDatabase;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.dao.StatusRenderDataDAO;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.List;

public class RoomDatabaseImpl implements RoomDatabaseService {
    private StatusRenderDataDAO statusRenderDataDAO;

    public RoomDatabaseImpl(Context context) {
        statusRenderDataDAO = TrafficDatabase.getInstance(context).getStatusRenderDataDAO();
    }

    @Override
    public void insertTrafficStatus(List<StatusRenderData> datas) {
        List<StatusRenderDataEntity> dataEntities = new ArrayList<>();
        for (StatusRenderData data : datas) {
            dataEntities.add(new StatusRenderDataEntity(data));
        }
        synchronized (this) {
            statusRenderDataDAO.insertStatusDatas(dataEntities);
        }
    }

    @Override
    public synchronized List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds) {
        return statusRenderDataDAO.getStatusByBounds();
    }
}
