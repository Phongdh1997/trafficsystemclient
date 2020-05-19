package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class TrafficTileLoader {
    public static final String TILE_LOADING = "tile_loading";
    public static final String TILE_LOADED = "tile_loaded";
    public static final String TILE_LOAD_FAIL = "tile_load_fail";

    private static final int LOAD_ZOOM = 14;

    private ThreadPoolExecutor executor = RetrofitClient.THREAD_POOL_EXECUTOR;
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private RoomDatabaseService roomDatabaseService;

    /**
     * Key: tile coordinates
     * Value: boolean: status of tile: true => loading; false => loaded
     */
    private HashMap<TileCoordinates, String> loadedTiles = new HashMap<>();

    /**
     * Load traffic status data for tileCoordinates
     * First: load from local.
     * If Empty then load from server
     * @param childTile
     */
    public List<StatusRenderData> loadTileData(@NotNull TileCoordinates childTile) {
        LatLngBounds childBounds = MyLatLngBoundsUtil.tileToLatLngBound(childTile);
        List<StatusRenderData> localDatas = loadTileDataFromLocal(childBounds);
        if (localDatas != null) {
            return localDatas;
        }

        // convert current tile to tile with zoom level LOAD_ZOOM
        // load data for parent tile from server
        LatLng centerPoint = childBounds.getCenter();
        TileCoordinates parentTile = MyLatLngBoundsUtil.getTileNumber(centerPoint.latitude, centerPoint.longitude, LOAD_ZOOM);
        synchronized (this) {
            String tileStatus = loadedTiles.get(parentTile);
            if (tileStatus == null) {
                loadMore(parentTile);
            } else {
                switch (tileStatus) {
                    case TILE_LOADING:
                    case TILE_LOADED:
                        break;
                    case TILE_LOAD_FAIL:
                        loadMore(parentTile);
                }
            }
        }
        return null;
    }

    private List<StatusRenderData> loadTileDataFromLocal(LatLngBounds bounds) {
        return roomDatabaseService.getTrafficStatus(bounds);
    }

    private void loadMore(@NotNull final TileCoordinates tileCoordinates) {
        loadedTiles.put(tileCoordinates, TILE_LOADING);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates);
                UserLocation userLocation = new UserLocation(bounds.getCenter());
                List<StatusRenderData> datas = statusRepositoryService.loadStatusRenderData(userLocation, tileCoordinates.z);
                if (datas != null) {
                    roomDatabaseService.insertTrafficStatus(datas);
                    loadedTiles.put(tileCoordinates, TILE_LOADED);
                } else {
                    loadedTiles.put(tileCoordinates, TILE_LOAD_FAIL);
                }
            }
        });
    }
}
