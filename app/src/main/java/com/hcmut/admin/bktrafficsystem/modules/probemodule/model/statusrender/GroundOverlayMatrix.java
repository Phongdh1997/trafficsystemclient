package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.Priority;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.PriorityFutureTask;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.PriorityRunable;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.LoadedTileManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.TrafficDataLoader;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroundOverlayMatrix {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    public static final int MATRIX_WIDTH = 3;

    private TrafficDataLoader trafficDataLoader;
    private TileRenderHandler tileRenderHandler;
    private static HashMap<TileCoordinates, GroundOverlayMatrixItem> matrix = new HashMap<>();

    public static GroundOverlayMatrixItem getMatrixItem(TileCoordinates tile) {
        return matrix.get(tile);
    }

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        trafficDataLoader = new TrafficDataLoader(context);
        tileRenderHandler = new MatrixRenderHandler(googleMap);
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> newTiles = preProccessMatrix(generateMatrixItems(centerTile));
        Log.e("matrix", "not loaded size: " + newTiles.size());
        for (TileCoordinates tile : newTiles) {
            renderTile(tile, tile.getTilePriority(centerTile));
        }
    }

    /**
     *
     * @param tileItems
     * @return not loaded tile
     */
    private List<TileCoordinates> preProccessMatrix(List<TileCoordinates> tileItems) {
        List<TileCoordinates> newTiles = new ArrayList<>();
        //HashMap<TileCoordinates, GroundOverlayMatrixItem> removeTiles = new HashMap<>();
        HashMap<TileCoordinates, GroundOverlayMatrixItem> newMatrix = new HashMap<>();
        for (TileCoordinates tile : tileItems) {
            if (matrix.containsKey(tile)) {
                newMatrix.put(tile, matrix.get(tile));
                matrix.remove(tile);
            } else {
                newTiles.add(tile);
            }
        }

        Iterator<GroundOverlayMatrixItem> removeItems = matrix.values().iterator();
        GroundOverlayMatrixItem matrixItem;
        for (TileCoordinates tile : newTiles) {
            if (removeItems.hasNext()) {
                matrixItem = removeItems.next();
                matrixItem.overlayInit();
                newMatrix.put(tile, matrixItem);
            } else {
                newMatrix.put(tile, new GroundOverlayMatrixItem());
            }
        }
        matrix = newMatrix;
        Log.e("matrix size", "" + matrix.size());
        return newTiles;
    }

    // TODO:
    public void refresh(final TileCoordinates centerTile) {

    }

    /**
     * TODO: Load tile to render
     *
     * if tile image is in Glide then call invalidate() to render tile
     * else load traffic data from server:
     * @param tile
     */
    private void renderTile(final TileCoordinates tile, Priority priority) {
        final GroundOverlayMatrixItem matrixItem = getMatrixItem(tile);
        if (matrixItem != null && matrixItem.isNotLoaded()) {
            matrixItem.ovelayLoading();
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new PriorityFutureTask(
                    new PriorityRunable(priority) {
                        @Override
                        public void run() {
                            List<StatusRenderDataEntity> datas = trafficDataLoader.loadTrafficData(tile);
                            if (datas != null && datas.size() > 0) {
                                tileRenderHandler.render(tile, datas);
                            } else {
                                matrixItem.overlayLoadFail();
                            }
                        }
                    }));
        }
    }

    private List<TileCoordinates> generateMatrixItems (TileCoordinates centerTile) {
        List<TileCoordinates> tileCoordinatesList = new ArrayList<>();
        List<TileCoordinates> rowItems = getRowItems(centerTile);
        for (TileCoordinates item : rowItems) {
            tileCoordinatesList.addAll(getColumnItems(item));
        }
        return tileCoordinatesList;
    }

    /**
     * get row items (contain center item)
     * @param center
     * @return
     */
    private List<TileCoordinates> getRowItems (TileCoordinates center) {
        List<TileCoordinates> tiles = new ArrayList<>();
        tiles.add(center);
        try {
            TileCoordinates leftTemp = center;
            TileCoordinates rightTemp = center;
            for (int i = 0; i < MATRIX_WIDTH / 2; i++) {
                leftTemp = leftTemp.getTileLeft();
                rightTemp = rightTemp.getTileRight();
                tiles.add(leftTemp);
                tiles.add(rightTemp);
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return tiles;
    }

    /**
     * get column items (contain center item)
     * @param center
     * @return
     */
    private List<TileCoordinates> getColumnItems (TileCoordinates center) {
        List<TileCoordinates> tiles = new ArrayList<>();
        tiles.add(center);
        try {
            TileCoordinates topTemp = center;
            TileCoordinates botTemp = center;
            for (int i = 0; i < MATRIX_WIDTH / 2; i++) {
                topTemp = topTemp.getTileTop();
                botTemp = botTemp.getTileBot();
                tiles.add(topTemp);
                tiles.add(botTemp);
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return tiles;
    }
}
