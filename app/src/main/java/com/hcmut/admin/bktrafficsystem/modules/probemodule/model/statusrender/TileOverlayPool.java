package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlay;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TileOverlayPool {
    private List<TileOverlay> idleOverlays = new ArrayList<>();
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
    public GroundOverlay poll (TileCoordinates centerTile) {
        Log.e("poolSize", "" + idleOverlays.size());
        if (idleOverlays.size() < minSize) return null;
        TileOverlay tileOverlay = getOverlayOutsideOfMatrix(centerTile);
        if (tileOverlay != null) {
            loadedTile.remove(tileOverlay.getTile());
            return tileOverlay.getGroundOverlay();
        }
        return null;
    }

    private TileOverlay getOverlayOutsideOfMatrix (TileCoordinates centerTile) {
        TileOverlay overlay = null;
        for (TileOverlay tileOverlay : idleOverlays) {
            if (tileOverlay.getTile().isOutsideOfMatrix(centerTile)) {
                overlay = tileOverlay;
                break;
            }
        }
        if (overlay != null) {
            idleOverlays.remove(overlay);
        }
        return overlay;
    }

    public void recycle(@NotNull TileCoordinates tile, @NotNull GroundOverlay groundOverlay) {
        idleOverlays.add(new TileOverlay(tile, groundOverlay));
    }
}