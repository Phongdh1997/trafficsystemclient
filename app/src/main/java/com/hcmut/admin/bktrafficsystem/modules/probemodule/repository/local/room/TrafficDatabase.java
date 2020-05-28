package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.dao.StatusRenderDataDAO;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

@Database(entities = {StatusRenderDataEntity.class}, exportSchema = false, version = 2)
public abstract class TrafficDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "traffic_db";

    private static TrafficDatabase trafficDatabase;

    public static TrafficDatabase getInstance(Context context) {
        if (trafficDatabase == null) {
            trafficDatabase = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TrafficDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return trafficDatabase;
    }

    //*************** define getter for DAO object here *******************//
    public abstract StatusRenderDataDAO getStatusRenderDataDAO();
}