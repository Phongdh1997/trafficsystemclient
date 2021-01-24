package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay;

import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class LoadedTileManager {
    public static final String LOADED_STATE = "LOADED_STATE";
    public static final String LOADING_STATE = "LOADING_STATE";
    public static final String LOAD_FAIL_STATE = "LOAD_FAIL_STATE";

    public static final int MAX_PERIOD_REFRESH_CACHE = 300000;

    private HashMap<TileCoordinates, String> loadedTiles = new HashMap<>();
    private static LoadedTileManager loadedTileManager;

    private LoadedTileManager() {}

    public static LoadedTileManager getInstance() {
        if (loadedTileManager == null) {
            loadedTileManager = new LoadedTileManager();
        }
        return loadedTileManager;
    }

    public synchronized void clear () {
        loadedTiles.clear();
    }

    public boolean checkClearCachedTile (TileCoordinates tile) {
        TileCoordinates cachedTile = null;
        for (TileCoordinates k : loadedTiles.keySet()) {
            if (k.equals(tile)) {
                cachedTile = k;
                break;
            }
        }
        if (cachedTile == null) {
            return false;
        }

        long nowMillisecond = Calendar.getInstance().getTime().getTime();
        return nowMillisecond - cachedTile.timestamp.getTime() > MAX_PERIOD_REFRESH_CACHE;
    }

    public boolean isNotLoaded (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                //return tileState.equals(LOAD_FAIL_STATE);
                return false;
            }
        }
        return true;
    }

    public synchronized boolean isLoaded (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADED_STATE);
            }
        }
        return false;
    }

    public synchronized boolean isLoading (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADING_STATE);
            }
        }
        return true;
    }

    public synchronized boolean isLoadingOrNotLoad(TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADING_STATE);
            }
        }
        return true;
    }

    public synchronized void setLoadedTile (TileCoordinates tile) {
        TileCoordinates cachedTile = null;
        for (TileCoordinates k : loadedTiles.keySet()) {
            if (k.equals(tile)) {
                cachedTile = k;
                break;
            }
        }
        if (cachedTile != null) {
            cachedTile.timestamp = Calendar.getInstance().getTime();
        }
        loadedTiles.put(tile, LOADED_STATE);
    }

    public synchronized void setLoadFailTile(TileCoordinates tile) {
        loadedTiles.put(tile, LOAD_FAIL_STATE);
    }

    public synchronized void setLoadingTile (TileCoordinates tile) {
        loadedTiles.put(tile, LOADING_STATE);
    }
}
