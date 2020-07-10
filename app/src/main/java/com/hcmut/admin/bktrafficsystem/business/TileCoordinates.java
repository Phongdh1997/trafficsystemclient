package com.hcmut.admin.bktrafficsystem.business;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.business.statusrender.GroundOverlayMatrix;
import com.hcmut.admin.bktrafficsystem.business.statusrender.StatusRender;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

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

    public boolean isOutsideOfMatrix (TileCoordinates centerTile) {
        return !isInsideOfMatrix(centerTile);
    }

    public boolean isInsideOfMatrix (TileCoordinates centerTile) {
        return getNearLevel(centerTile) < (GroundOverlayMatrix.MATRIX_WIDTH / 2 + 1);
    }

    /**
     *
     * @param googleMap
     * @return
     */
    public static TileCoordinates getCenterTile (@NotNull GoogleMap googleMap) {
        try {
            LatLng target = googleMap.getCameraPosition().target;
            return MyLatLngBoundsUtil.getTileNumber(
                    target.latitude,
                    target.longitude,
                    StatusRender.TILE_ZOOM_LEVEL);
        } catch (TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param tile
     * @return  how far does this tile is to the given tile
     */
    public int getNearLevel (@NotNull TileCoordinates tile) {
        if (x == tile.x) {
            return Math.abs(y - tile.y);
        } else {
            return Math.abs(x - tile.x);
        }
    }

    /**
     * Get tile priority with given CenterTile
     * IMMEDIATE:   Tile is near centerTile 1 level
     * HIGH:        Tile is near centerTile 2 level
     * MEDIUM:      Tile is near centerTile 3 level
     * LOW: everything else
     * @param centerTile
     * @return
     */
    public Priority getTilePriority (TileCoordinates centerTile) {
        switch (getNearLevel(centerTile)) {
            case 0:
            case 1:
                return Priority.IMMEDIATE;
            case 2:
                return Priority.HIGH;
            case 3:
                return Priority.MEDIUM;
            default:
                return Priority.LOW;
        }
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
