package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlay;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TileOverlayPool {
    private Queue<TileOverlay> idleOverlays = new LinkedList<>();
    private HashMap<TileCoordinates, String> loadedTile;
    private final int minSize = 30;

    public TileOverlayPool(HashMap<TileCoordinates, String> loadedTile) {
        this.loadedTile = loadedTile;
    }

    /**
     * Take an idle Overlay from idle Overlay list
     * Remove that tile from loadedTile
     * @return  GroundOverlay. return null if have not idle Overlay
     */
    public GroundOverlay poll () {
        Log.e("poolSize", "" + idleOverlays.size());
        if (idleOverlays.size() < minSize) return null;
        TileOverlay tileOverlay = idleOverlays.poll();
        if (tileOverlay != null) {
            loadedTile.remove(tileOverlay.getTile());
            return tileOverlay.getGroundOverlay();
        }
        return null;
    }

    public void recycle(@NotNull TileCoordinates tile, @NotNull GroundOverlay groundOverlay) {
        idleOverlays.add(new TileOverlay(tile, groundOverlay));
    }
}
