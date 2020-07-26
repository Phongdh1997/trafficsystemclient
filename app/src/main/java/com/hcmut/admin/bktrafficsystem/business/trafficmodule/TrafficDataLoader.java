package com.hcmut.admin.bktrafficsystem.business.trafficmodule;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender.GroundOverlayMatrix;
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
    public static final int LOAD_TILE_LEVEL = 15;
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
        tile = MyLatLngBoundsUtil.convertTile(tile, 15);
        if (!tileLoadFinishListeners.containsKey(tile)) {
            tileLoadFinishListeners.put(tile, callback);
        }
    }

    /**
     *
     * @param centerTile: tile zoom level 15
     */
    public void loadPaddingTrafficData(TileCoordinates centerTile) {
        centerTile = MyLatLngBoundsUtil.convertTile(centerTile, 15);
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

    public void loadTrafficDataFromServerAsync(TileCoordinates tile, final boolean isTrackState) {
        final TileCoordinates tile_15 = MyLatLngBoundsUtil.convertTile(tile, 15);
        if (isTrackState && loadedTileManager.isLoadingOrNotLoad(tile_15)) {
            dataLoadingState.putLoadingTile(tile_15);
        }
        if (loadedTileManager.isNotLoaded(tile_15)) {
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loadedTileManager.setLoadingTile(tile_15);
                    LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tile_15);
                    UserLocation userLocation = new UserLocation(bounds.getCenter());
                    List<StatusRenderData> dataList = statusRepositoryService
                            .loadStatusRenderData(userLocation, getTileRadius(tile_15));
                    if (dataList != null) {
                        loadedTileManager.setLoadedTile(tile_15);
                        List<StatusRenderDataEntity> entities = StatusRenderDataEntity.parseStatusRenderDataEntity(dataList);
                        roomDatabaseService.insertTrafficStatus(entities);
                        TileLoadFinishCallback callback = tileLoadFinishListeners.get(tile_15);
                        if (callback != null) {
                            callback.onSuccess(tile_15, entities);
                            tileLoadFinishListeners.remove(tile_15);
                        }
                    } else {
                        loadedTileManager.setLoadFailTile(tile_15);
                    }
                    if (dataLoadingState != null) {
                        dataLoadingState.removeLoadingTile(tile_15);
                    }
                    Log.e("tile status", tile_15.toString() + "loaded, " + userLocation.toString());
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
        void onSuccess(TileCoordinates tile, List<StatusRenderDataEntity> entities);
    }
}
