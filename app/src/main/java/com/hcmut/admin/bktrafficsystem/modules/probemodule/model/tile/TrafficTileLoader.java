package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.RoomDatabaseImpl;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
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

    /**
     * radius of tile level in meters
     * ref: https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/
     */
    private static final int TILE_RADIUS_LEVEL_14 = 1730;
    private static final int TILE_RADIUS_LEVEL_15 = 865;
    private static final int LOAD_ZOOM = 14;

    private ThreadPoolExecutor executor = RetrofitClient.THREAD_POOL_EXECUTOR;
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private RoomDatabaseService roomDatabaseService;

    /**
     * Key: tile coordinates
     * Value: TILE_LOADING, TILE_LOADED, TILE_LOAD_FAIL
     */
    private HashMap<TileCoordinates, String> loadedTiles = new HashMap<>();

    public synchronized void clearTileDataCached() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedTiles.clear();
                roomDatabaseService.deleteAll();
            }
        });
    }

    public TrafficTileLoader(Context context) {
        roomDatabaseService = new RoomDatabaseImpl(context);
    }

    /**
     * Load data for tileCoordinates from local
     * if tile not loaded then load from server
     *
     * @param renderTile
     * @return
     */
    public List<StatusRenderDataEntity> loadData(TileCoordinates renderTile) {
        final LatLngBounds renderLatLngBounds = MyLatLngBoundsUtil.tileToLatLngBound(renderTile);
        final LatLng centerPoint = renderLatLngBounds.getCenter();
        try {
            TileCoordinates tileWithLOAD_ZOOM = MyLatLngBoundsUtil.getTileNumber(centerPoint.latitude, centerPoint.longitude, LOAD_ZOOM);
            String tileStatus = loadedTiles.get(tileWithLOAD_ZOOM);
            if (tileStatus == null) {
                loadDataFromServer(tileWithLOAD_ZOOM);
            } else if (tileStatus.equals(TILE_LOADED)) {
                return roomDatabaseService.getTrafficStatus(renderLatLngBounds);
            }
        } catch (Exception e) {}
        return null;
    }

    /**
     * Load tile unit (tile in zoom level LOAD_ZOOM) from server
     * The number of tiles: 9 tile (1 current tile, 8 tile padding)
     *
     * @param tile: tile need to load data
     */
    private void loadDataFromServer(TileCoordinates tile) {
        try {
            loadTileData(tile);  // load current tile

            // load 8 padding tile
            loadTileData(TileCoordinates.getTileCoordinates(tile.x, tile.y - 1, tile.z));         // top
            loadTileData(TileCoordinates.getTileCoordinates(tile.x, tile.y + 1, tile.z));         // bot
            loadTileData(TileCoordinates.getTileCoordinates(tile.x - 1, tile.y, tile.z));         // left
            loadTileData(TileCoordinates.getTileCoordinates(tile.x + 1, tile.y, tile.z));         // right
            loadTileData(TileCoordinates.getTileCoordinates(tile.x - 1, tile.y - 1, tile.z));  // top-left
            loadTileData(TileCoordinates.getTileCoordinates(tile.x + 1, tile.y - 1, tile.z));  // top-right
            loadTileData(TileCoordinates.getTileCoordinates(tile.x + 1, tile.y + 1, tile.z));  // bot-right
            loadTileData(TileCoordinates.getTileCoordinates(tile.x - 1, tile.y + 1, tile.z));  // bot-left
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {}
    }

    private void loadTileData(TileCoordinates tile) {
        String tileStatus = loadedTiles.get(tile);
        if (tileStatus == null) {
            loadMore(tile);
        } else {
            switch (tileStatus) {
                case TILE_LOADING:
                case TILE_LOADED:
                case TILE_LOAD_FAIL:
                    break;
            }
        }
    }

    private void loadMore(@NotNull final TileCoordinates tileCoordinates) {
        loadedTiles.put(tileCoordinates, TILE_LOADING);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates);
                UserLocation userLocation = new UserLocation(bounds.getCenter());
                Log.e("tile status", tileCoordinates.toString() + "loading, " + userLocation.toString());
                List<StatusRenderData> datas = statusRepositoryService.loadStatusRenderData(userLocation, TILE_RADIUS_LEVEL_14);
                if (datas != null) {
                    roomDatabaseService.insertTrafficStatus(datas);
                    loadedTiles.put(tileCoordinates, TILE_LOADED);
                    Log.e("tile status", tileCoordinates.toString() + "loaded");
                    Log.e("loaded", "status size " + datas.size());

                } else {
                    loadedTiles.put(tileCoordinates, TILE_LOAD_FAIL);
                    Log.e("tile status", tileCoordinates.toString() + "load fail, " + userLocation.toString());
                }
            }
        });
    }
}
