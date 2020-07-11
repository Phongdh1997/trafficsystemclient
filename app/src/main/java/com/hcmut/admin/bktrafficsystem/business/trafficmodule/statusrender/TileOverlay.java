package com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender;

import com.google.android.gms.maps.model.GroundOverlay;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;

import org.jetbrains.annotations.NotNull;

public class TileOverlay {
    public GroundOverlay groundOverlay;
    public TileCoordinates tile;

    public TileOverlay(@NotNull TileCoordinates tile, @NotNull GroundOverlay groundOverlay) {
        this.tile = tile;
        this.groundOverlay = groundOverlay;
    }

    public GroundOverlay getGroundOverlay() {
        return groundOverlay;
    }

    public void setGroundOverlay(GroundOverlay groundOverlay) {
        this.groundOverlay = groundOverlay;
    }

    public TileCoordinates getTile() {
        return tile;
    }

    public void setTile(TileCoordinates tile) {
        this.tile = tile;
    }
}
