package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.Priority;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.PriorityFutureTask;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.PriorityRunable;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroundOverlayMatrix {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    private HashMap<TileCoordinates, String> tileStates = new HashMap<>();
    private TrafficLoader trafficLoader;
    private TileRenderHandler tileRenderHandler;
    private GlideBitmapHelper glideBitmapHelper;

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        trafficLoader = new TrafficLoader();
        tileRenderHandler = new BitmapTileRenderHandlerImpl(googleMap, tileStates);
        glideBitmapHelper = GlideBitmapHelper.getInstance(context.getApplicationContext());
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> tileItems = generateMatrixItems(centerTile);
        Log.e("matrix", "not loaded size: " + tileItems.size());
        for (TileCoordinates tile : tileItems) {
            renderTile(tile, tile.getTilePriority(centerTile));
        }
    }

    /**
     * TODO: Load tile to render
     *
     * if tile image is in Glide then call invalidate() to render tile
     * else load traffic data from server:
     * @param tile
     */
    private void renderTile(TileCoordinates tile, Priority priority) {
        String tileState = tileStates.get(tile);
        if (tileState == null) {
            handleRenderTile(tile, priority);
        } else {
            switch (tileState) {
                case LOADING_OVERLAY:
                    break;
                case LOADED_OVERLAY:
                    Log.e("maxtrix", "loaded");
                    break;
                case LOAD_FAIL_OVERLAY:
                    handleRenderTile(tile, priority);
                    break;
            }
        }
    }

    /**
     * Load data from server
     * Dispatch loaded data to TileRenderHandler
     * @param tile
     */
    private void handleRenderTile (final TileCoordinates tile, Priority priority) {
        Bitmap bitmap = glideBitmapHelper.loadBitmapFromGlide(tile);
        if (bitmap != null) {
            Log.e("glide", "have image");
            tileRenderHandler.render(tile, bitmap, tileStates);
        } else {
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new PriorityFutureTask(
                    new PriorityRunable(priority) {
                        @Override
                        public void run() {
                            tileStates.put(tile, LOADING_OVERLAY);
                            List<StatusRenderData> datas = trafficLoader.loadDataFromServer(tile);
                            if (datas != null) {
                                Bitmap returnBitmap = tileRenderHandler.render(tile, datas, tileStates);
                                glideBitmapHelper.storeBitmapToGlide(tile, returnBitmap);
                            } else {
                                tileStates.put(tile, LOAD_FAIL_OVERLAY);
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
            TileCoordinates left = center.getTileLeft();
            TileCoordinates right = center.getTileRight();
            tiles.add(left);
            tiles.add(right);
            tiles.add(left.getTileLeft());
            tiles.add(right.getTileRight());
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
            TileCoordinates top = center.getTileTop();
            TileCoordinates bot = center.getTileBot();
            tiles.add(top);
            tiles.add(bot);
            tiles.add(top.getTileTop());
            tiles.add(bot.getTileBot());
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return tiles;
    }
}
