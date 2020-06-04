package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TileCoordinates {
    public final int x;
    public final int y;
    public final int z;

    private TileCoordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static TileCoordinates getTileCoordinates(int x, int y, int z) throws TileCoordinatesNotValid {
        double maxHeight = Math.pow(2, z) - 1;
        if (x < 0 || y < 0 || x > maxHeight || y > maxHeight) {
            throw new TileCoordinatesNotValid();
        }
        return new TileCoordinates(x, y, z);
    }

    public TileCoordinates getTileLeft() throws TileCoordinatesNotValid {
        return TileCoordinates.getTileCoordinates(x - 1, y, z);
    }

    public TileCoordinates getTileRight() throws TileCoordinatesNotValid {
        return TileCoordinates.getTileCoordinates(x + 1, y, z);
    }

    public TileCoordinates getTileTop() throws TileCoordinatesNotValid {
        return TileCoordinates.getTileCoordinates(x, y - 1, z);
    }

    public TileCoordinates getTileBot() throws TileCoordinatesNotValid {
        return TileCoordinates.getTileCoordinates(x, y + 1, z);
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
        return "(x: " + x + ", y: " + y + ", z: " + z + ")";
    }

    public static class TileCoordinatesNotValid extends Exception {
        @Override
        public String getMessage() {
            return "Tile Coordinates is not valid";
        }
    }
}
