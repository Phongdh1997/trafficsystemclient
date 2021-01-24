package com.hcmut.admin.bktrafficsystem.business.trafficmodule;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay.LoadedTileManager;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay.TrafficTileProvider;
import com.hcmut.admin.bktrafficsystem.repository.RoomDatabaseService;
import com.hcmut.admin.bktrafficsystem.repository.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.repository.local.RoomDatabaseImpl;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import java.util.List;

public class TrafficDataLoader {
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private RoomDatabaseService roomDatabaseService;
    private LoadedTileManager loadedTileManager;

    private ClearCacheCallback clearCacheCallback;

    private TrafficDataLoader(Context context) {
        roomDatabaseService = new RoomDatabaseImpl(context);
        loadedTileManager = LoadedTileManager.getInstance();
    }

    private static TrafficDataLoader trafficDataLoader;
    public static TrafficDataLoader getInstance(Context context) {
        if (trafficDataLoader == null) {
            trafficDataLoader = new TrafficDataLoader(context);
        }
        return trafficDataLoader;
    }

    public void setClearCacheCallback(ClearCacheCallback callback) {
        clearCacheCallback = callback;
    }

    /**
     * Load data from server if not exist,
     * Else load data from local
     * @param renderTile
     * @return
     */
    public List<StatusRenderDataEntity> loadTrafficDataFromServer(TileCoordinates renderTile) {
        final TileCoordinates loadingTile = MyLatLngBoundsUtil.convertTile(renderTile, getLoadTileZoomLevel(renderTile));
        if (loadingTile == null) {
            return null;
        }
        final boolean isNotLoaded;
        synchronized (this) {
            isNotLoaded = loadedTileManager.isNotLoaded(loadingTile);
            if (isNotLoaded) {
                loadedTileManager.setLoadingTile(loadingTile);
            }
        }
        if (isNotLoaded) {
            loadedTileManager.setLoadingTile(loadingTile);
            LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(loadingTile);
            UserLocation userLocation = new UserLocation(bounds.getCenter());
            List<StatusRenderData> dataList = statusRepositoryService.loadStatusRenderData(
                    userLocation, getTileRadius(loadingTile), getStreetLevel(loadingTile));
            List<StatusRenderDataEntity> dataEntities = StatusRenderDataEntity.parseStatusRenderDataEntity(dataList);
            roomDatabaseService.insertTrafficStatus(dataEntities);
            loadedTileManager.setLoadedTile(loadingTile);
            Log.e("tile loaded", loadingTile.toString());
            return dataEntities;
        }

        // wait loading data from server
        for (int i = 0; i < 10; i++) {
            if (loadedTileManager.isLoaded(loadingTile)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Kiểm tra timestamp của tile có vượt quá chu kì tính toán vận tốc TB là 5 phút hay không.
        if (loadedTileManager.checkClearCachedTile(loadingTile)) {
            if (clearCacheCallback != null) {
                clearCacheCallback.onClearCache();
            }
        }

        return loadDataFromLocal(renderTile);
    }

    /**
     * Load data for one large area
     * @return
     */
    public List<StatusRenderDataEntity> loadTrafficDataForHCMCityFromServer(TileCoordinates renderTile) {
        TileCoordinates loadingTile = TileCoordinates.getHCMCityTileCoordinates();
        if (loadingTile == null) {
            return null;
        }
        final boolean isNotLoaded;
        synchronized (this) {
            isNotLoaded = loadedTileManager.isNotLoaded(loadingTile);
            if (isNotLoaded) {
                loadedTileManager.setLoadingTile(loadingTile);
            }
        }
        if (isNotLoaded) {
            List<StatusRenderData> dataList = statusRepositoryService.loadStatusRenderData(
                    new UserLocation(TileCoordinates.getHCMCityCenterPoint()), TileCoordinates.HCM_CITY_RADIUS, 1);
            List<StatusRenderDataEntity> dataEntities = StatusRenderDataEntity.parseStatusRenderDataEntity(dataList);
            roomDatabaseService.insertTrafficStatus(dataEntities);
            loadedTileManager.setLoadedTile(loadingTile);
            Log.e("tile loaded", loadingTile.toString());
            return dataEntities;
        }

        // wait loading data from server
        for (int i = 0; i < 10; i++) {
            if (loadedTileManager.isLoaded(loadingTile)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return roomDatabaseService.getTrafficStatus(MyLatLngBoundsUtil.tileToLatLngBound(renderTile), getStreetTypeName(1));
    }

    public List<StatusRenderDataEntity> loadDataFromLocal(TileCoordinates tile) {
        int streetLevel = getStreetLevel(tile);
        if (streetLevel > 0) {
            return roomDatabaseService.getTrafficStatus(
                    MyLatLngBoundsUtil.tileToLatLngBound(tile),
                    getStreetTypeName(streetLevel));
        }
        return roomDatabaseService.getTrafficStatus(MyLatLngBoundsUtil.tileToLatLngBound(tile));
    }

    public boolean isDataNotLoaded(TileCoordinates tile) {
        return loadedTileManager.isNotLoaded(MyLatLngBoundsUtil.convertTile(tile, getLoadTileZoomLevel(tile)));
    }

    /**
     * ref: https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/
     * @param tile
     * @return radius of tile level in) meters
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

    private int getLoadTileZoomLevel(TileCoordinates tile) {
        if (tile.z > 14) {
            return 15;
        } else if (tile.z > 12) {
            return 13;
        }
        return 15;
    }

    private int getStreetLevel(TileCoordinates tile) {
        if (tile.z > 14) {
            return 0;
        } else if (tile.z > 12) {
            return 2;
        }
        return 1;
    }

    private String getStreetTypeName(int streetLevel) {
        switch (streetLevel) {
            case 1:
                return "trunk";
            case 2:
                return "primary";
        }
        return "trunk";
    }

    public interface ClearCacheCallback {
        void onClearCache();
    }
}
