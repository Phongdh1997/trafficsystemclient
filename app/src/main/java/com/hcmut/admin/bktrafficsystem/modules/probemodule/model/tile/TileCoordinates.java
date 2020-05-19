package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TileCoordinates {
    public final int x;
    public final int y;
    public final int z;

    public TileCoordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        return x + y + z;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TileCoordinates) {
            TileCoordinates otherTile = (TileCoordinates) obj;
            return x == otherTile.x && y == otherTile.y && z == otherTile.z;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "x: " + x + ", y: " + y + ", z: " + z;
    }
}
