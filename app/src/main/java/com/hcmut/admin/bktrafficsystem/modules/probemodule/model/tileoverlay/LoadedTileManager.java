package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;

import java.util.HashMap;

public class LoadedTileManager {
    public static final String LOADED_STATE = "LOADED_STATE";
    public static final String LOADING_STATE = "LOADING_STATE";
    public static final String LOAD_FAIL_STATE = "LOAD_FAIL_STATE";

    private HashMap<TileCoordinates, String> loadedTiles = new HashMap<>();

    public synchronized boolean isNotLoaded (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                return tileState.equals(LOAD_FAIL_STATE);
            }
        }
        return true;
    }

    public synchronized void setLoadedTile (TileCoordinates tile) {
        loadedTiles.put(tile, LOADED_STATE);
    }

    public synchronized void setLoadFailTile(TileCoordinates tile) {
        loadedTiles.put(tile, LOAD_FAIL_STATE);
    }

    public synchronized void setLoadingTile (TileCoordinates tile) {
        loadedTiles.put(tile, LOADING_STATE);
    }
}
