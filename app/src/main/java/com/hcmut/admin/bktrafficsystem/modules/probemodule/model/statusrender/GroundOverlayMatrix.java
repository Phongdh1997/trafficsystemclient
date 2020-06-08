package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.request.FutureTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide.BitmapGlideModel;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide.GlideApp;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class GroundOverlayMatrix {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    private HashMap<TileCoordinates, String> tileStates = new HashMap<>();
    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficLoader trafficLoader;
    private GlideBitmapHelper glideBitmapHelper;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficLoader = new TrafficLoader(this, context);
        glideBitmapHelper = GlideBitmapHelper.getInstance(context);
        glideBitmapHelper.clearDiskCache(RetrofitClient.THREAD_POOL_EXECUTOR);
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> tileItems = generateMatrixItems(centerTile);
        Log.e("matrix", "not loaded size: " + tileItems.size());
        for (TileCoordinates tile : tileItems) {
            renderTile(tile);
        }
    }

    /**
     * TODO: Load tile to render
     *
     * if tile image is in Glide then call invalidate() to render tile
     * else load traffic data from server:
     * @param tile
     */
    private void renderTile(TileCoordinates tile) {
        String tileState = tileStates.get(tile);
        if (tileState == null) {
            tileStates.put(tile, LOADING_OVERLAY);
            trafficLoader.loadDataFromServer(tile, tileStates);
        } else {
            switch (tileState) {
                case LOADING_OVERLAY:
                    break;
                case LOADED_OVERLAY:
                    Log.e("maxtrix", "loaded");
                    break;
                case LOAD_FAIL_OVERLAY:
                    tileStates.put(tile, LOADING_OVERLAY);
                    trafficLoader.loadDataFromServer(tile, tileStates);
                    break;
            }
        }
    }

    /**
     * Invalidate matrix item
     * if bitmap is null then load from Glide
     * @param tileCoordinates
     * @param bitmap
     */
    public void invalidate(TileCoordinates tileCoordinates, @Nullable Bitmap bitmap) {
        if (bitmap != null) {
            performRenderBitmapToTile(tileCoordinates, bitmap);
        }
    }

    /**
     * Trigger to refresh tile with available bitmap
     * tile is rendered when it is in matrix
     */
    private void performRenderBitmapToTile(final TileCoordinates target, @NotNull Bitmap bitmap) {
        final GoogleMap googleMap = googleMapWeakReference.get();
        if (googleMap != null) {
            final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    googleMap.addGroundOverlay(groundOverlayOptions);
                    tileStates.put(target, LOADED_OVERLAY);
                }
            });
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
