package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

import java.util.List;

@Dao
public interface StatusRenderDataDAO {

    @Query("SELECT * FROM status_render_data WHERE " +
        "((:top > startLat AND startLat > :bot) AND (:left < startLng AND startLng < :right)) OR " +
        "((:top > endLat AND endLat > :bot) AND (:left < endLng AND endLng < :right))")
    List<StatusRenderDataEntity> getStatusByBounds(double top, double right, double bot, double left);

    @Query("SELECT * FROM status_render_data")
    List<StatusRenderDataEntity> getStatus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatusDatas(List<StatusRenderDataEntity> datas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatusData(StatusRenderDataEntity data);

    @Query("DELETE FROM status_render_data")
    void deleteAllRecord();
}
