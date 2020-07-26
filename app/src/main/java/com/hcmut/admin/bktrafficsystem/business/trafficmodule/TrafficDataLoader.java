package com.hcmut.admin.bktrafficsystem.business.trafficmodule;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.groundoverlay.GroundOverlayMatrix;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay.LoadedTileManager;
import com.hcmut.admin.bktrafficsystem.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.repository.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.repository.local.RoomDatabaseImpl;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficDataLoader {
    public static final int LOAD_TILE_LEVEL = 14;
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private RoomDatabaseService roomDatabaseService;
    private LoadedTileManager loadedTileManager;
    private Map<TileCoordinates, TileLoadFinishCallback> tileLoadFinishListeners = new HashMap<>();
    private DataLoadingState dataLoadingState;

    private TrafficDataLoader(Context context) {
        roomDatabaseService = new RoomDatabaseImpl(context);
        loadedTileManager = LoadedTileManager.getInstance();
    }

    public void setDataLoadingState(DataLoadingState dataLoadingState) {
        this.dataLoadingState = dataLoadingState;
    }

    private static TrafficDataLoader trafficDataLoader;
    public static TrafficDataLoader getInstance(Context context) {
        if (trafficDataLoader == null) {
            trafficDataLoader = new TrafficDataLoader(context);
        }
        return trafficDataLoader;
    }

    public void addTileLoadFinishListener(TileCoordinates tile, TileLoadFinishCallback callback) {
        if (!tileLoadFinishListeners.containsKey(tile)) {
            tileLoadFinishListeners.put(tile, callback);
        }
    }

    /**
     *
     * @param centerTile: tile zoom level 15
     */
    public void loadPaddingTrafficData(TileCoordinates centerTile) {
        centerTile = MyLatLngBoundsUtil.convertTile(centerTile, LOAD_TILE_LEVEL);
        List<TileCoordinates> tileList = GroundOverlayMatrix.generateMatrixItems(centerTile, 5, false);
        for (final TileCoordinates tile : tileList) {
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loadTrafficDataFromServerAsync(tile, false);
                }
            });
        }
    }

    public synchronized void loadTrafficDataFromServerAsync(TileCoordinates tile, final boolean isTrackState) {
        final TileCoordinates loadTile = MyLatLngBoundsUtil.convertTile(tile, LOAD_TILE_LEVEL);
        if (isTrackState && loadedTileManager.isLoadingOrNotLoad(loadTile)) {
            dataLoadingState.putLoadingTile(loadTile);
        }
        if (loadedTileManager.isNotLoaded(loadTile)) {
            loadedTileManager.setLoadingTile(loadTile);
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(loadTile);
                    UserLocation userLocation = new UserLocation(bounds.getCenter());
                    List<StatusRenderData> dataList = statusRepositoryService
                            .loadStatusRenderData(userLocation, getTileRadius(loadTile));
                    if (dataList != null) {
                        loadedTileManager.setLoadedTile(loadTile);
                        List<StatusRenderDataEntity> entities = StatusRenderDataEntity.parseStatusRenderDataEntity(dataList);
                        roomDatabaseService.insertTrafficStatus(entities);
                        TileLoadFinishCallback callback = tileLoadFinishListeners.get(loadTile);
                        if (callback != null) {
                            callback.onSuccess(entities);
                            tileLoadFinishListeners.remove(loadTile);
                        }
                    } else {
                        loadedTileManager.setLoadFailTile(loadTile);
                    }
                    if (dataLoadingState != null) {
                        dataLoadingState.removeLoadingTile(loadTile);
                    }
                    Log.e("tile status", loadTile.toString() + "loaded, " + userLocation.toString());
                }
            });
        }
    }

    public List<StatusRenderDataEntity> loadDataFromLocal(TileCoordinates tile) {
        return roomDatabaseService.getTrafficStatus(MyLatLngBoundsUtil.tileToLatLngBound(tile));
    }

    public boolean isDataNotLoaded(TileCoordinates tile) {
        return loadedTileManager.isNotLoaded(tile);
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

    public interface TileLoadFinishCallback {
        void onSuccess(List<StatusRenderDataEntity> entities);
    }
}
