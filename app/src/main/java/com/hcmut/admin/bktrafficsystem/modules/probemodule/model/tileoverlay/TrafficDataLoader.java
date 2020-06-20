package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.BitmapLineData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.RoomDatabaseImpl;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import java.util.List;

public class TrafficDataLoader {
    public static final int LOAD_TILE_LEVEL = 15;
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private LoadedTileManager loadedTileManager;
    private RoomDatabaseService roomDatabaseService;

    public TrafficDataLoader (Context context) {
        loadedTileManager = new LoadedTileManager();
        roomDatabaseService = new RoomDatabaseImpl(context);
    }

    public void notifyDataChange () {
        loadedTileManager.clear();
    }

    public List<StatusRenderDataEntity> loadTrafficData (TileCoordinates renderTile) {
        TileCoordinates loadTile = MyLatLngBoundsUtil.convertTile(renderTile, LOAD_TILE_LEVEL);
        if (loadedTileManager.isNotLoaded(loadTile)) {
            Log.e("tile", "loading");
            loadedTileManager.setLoadingTile(loadTile);
            List<StatusRenderData> serverDatas = loadDataFromServer(loadTile);
            if (serverDatas != null && serverDatas.size() > 0) {
                List<StatusRenderDataEntity> dataEntities = StatusRenderDataEntity
                        .parseStatusRenderDataEntity(serverDatas);
                roomDatabaseService.insertTrafficStatus(dataEntities);
                loadedTileManager.setLoadedTile(loadTile);
                Log.e("tile", "loaded from server");
                return dataEntities;
            } else {
                loadedTileManager.setLoadFailTile(loadTile);
                Log.e("tile", "load fail");
            }
        } else {
            Log.e("tile", "loaded from db");
            return roomDatabaseService.getTrafficStatus(
                    MyLatLngBoundsUtil.tileToLatLngBound(renderTile));
        }
        return null;
    }

    /**
     * load traffic data from server
     * if success then draw bitmap
     * else ...
     * @param tile
     * @return
     */
    private List<StatusRenderData> loadDataFromServer(final TileCoordinates tile) {
        LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tile);
        UserLocation userLocation = new UserLocation(bounds.getCenter());
        Log.e("tile status", tile.toString() + "loading, " + userLocation.toString());
        return statusRepositoryService.loadStatusRenderData(userLocation, getTileRadius(tile));
    }

    /**
     * ref: https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/
     * @param tile
     * @return radius of tile level in meters
     */
    private int getTileRadius (TileCoordinates tile) {
        double resolution = 1;  // meters / pixel
        switch (tile.z) {
            case 13:
                resolution = 19.10925707;
                break;
            case 14:
                resolution = 9.554728536;
                break;
            case 15:
                resolution = 4.777314268;
                break;
            case 16:
                resolution = 2.388657133;
                break;
            case 17:
                resolution = 1.194328566;
                break;
            default:
        }
        double weith = resolution * 256;
        return (int) (Math.sqrt(weith * weith * 2) / 2);
    }
}
