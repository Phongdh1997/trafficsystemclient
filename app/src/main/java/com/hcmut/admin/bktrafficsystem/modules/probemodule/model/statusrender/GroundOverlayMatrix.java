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
import java.util.HashMap;
import java.util.List;

public class GroundOverlayMatrix {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    public static final int MATRIX_WIDTH = 3;

    TrafficDataLoader trafficDataLoader;
    private TileRenderHandler tileRenderHandler;

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        trafficDataLoader = new TrafficDataLoader(context);
        tileRenderHandler = new BitmapTileRenderHandlerImpl(googleMap);
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> tileItems = generateMatrixItems(centerTile);
        Log.e("matrix", "not loaded size: " + tileItems.size());
        for (TileCoordinates tile : tileItems) {
            renderTile(tile, tile.getTilePriority(centerTile));
        }
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
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new PriorityFutureTask(
                new PriorityRunable(priority) {
                    @Override
                    public void run() {
                        List<StatusRenderDataEntity> datas = trafficDataLoader.loadTrafficData(tile);
                        tileRenderHandler.render(tile, datas);
                    }
                }));
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
