package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.graphics.Bitmap;
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
    private final int matrixSize = 5;   // M = N = matrixSize
    private HashMap<TileCoordinates, GroundOverlayMatrixItem> overlayMatrix = new HashMap<>();
    private Queue<GroundOverlayMatrixItem> idleOverlay = new LinkedList<>();
    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficLoader trafficLoader;
    private GlideBitmapHelper glideBitmapHelper;

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficLoader = new TrafficLoader(this, context);
        glideBitmapHelper = GlideBitmapHelper.getInstance(context);
        glideBitmapHelper.clearDiskCache(RetrofitClient.THREAD_POOL_EXECUTOR);
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> notLoadedTile = getNotLoadedTile(centerTile);
        Log.e("matrix", "not loaded size: " + notLoadedTile.size());
        Log.e("matrix", "idle size: " + idleOverlay.size());
        for (TileCoordinates tile : notLoadedTile) {
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
        Bitmap bitmap = glideBitmapHelper.loadBitmapFromGlide(tile);
        if (bitmap != null) {   // bitmap is in Glide
            invalidate(tile, bitmap);
            Log.e("matrix", "have data in glide");
        } else {
            GroundOverlayMatrixItem renderMatrixItem = overlayMatrix.get(tile);
            if (renderMatrixItem != null) {
                switch (renderMatrixItem.getState()) {
                    case GroundOverlayMatrixItem.INIT_OVERLAY:
                        renderMatrixItem.setState(GroundOverlayMatrixItem.LOADING_OVERLAY);
                        trafficLoader.loadDataFromServer(tile);
                        break;
                    case GroundOverlayMatrixItem.LOADING_OVERLAY:
                        break;
                    case GroundOverlayMatrixItem.LOADED_OVERLAY:
                        break;
                    case GroundOverlayMatrixItem.LOAD_FAIL_OVERLAY:
                        break;
                }
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
        Bitmap renderBitmap = bitmap;
        if (renderBitmap == null) {
            renderBitmap = glideBitmapHelper.loadBitmapFromGlide(tileCoordinates);
        }
        if (renderBitmap != null) {
            renderBitmapToTile(tileCoordinates, renderBitmap);
        }
    }

    /**
     * Trigger to refresh tile with available bitmap
     * tile is rendered when it is in matrix
     */
    private void renderBitmapToTile(TileCoordinates tileCoordinates, @NotNull Bitmap bitmap) {
        GoogleMap googleMap = googleMapWeakReference.get();
        if (googleMap == null) return;

        GroundOverlayMatrixItem renderMatrixItem = overlayMatrix.get(tileCoordinates);
        if (renderMatrixItem != null && !renderMatrixItem.isOvelayLoaded()) {   // tile coordinates is in matrix
            // find an available ground overlay to this tile
            GroundOverlayMatrixItem idleMatrixItem = idleOverlay.poll();
            if (idleMatrixItem != null) {  // if have available Overlay
                renderMatrixItem.invalidate(bitmap, tileCoordinates, idleMatrixItem);
            } else {    // create new overlay
                renderMatrixItem.invalidate(bitmap, tileCoordinates, googleMap);
            }
        }
    }

    /**
     * Create new matrix and add idleOverlay
     * @param centerTile
     * @return not loaded tile
     */
    private List<TileCoordinates> getNotLoadedTile (TileCoordinates centerTile) {
        HashMap<TileCoordinates, GroundOverlayMatrixItem> newMatrixItems = generateMatrixItems(centerTile);
        if (overlayMatrix.size() == 0) {
            overlayMatrix = newMatrixItems;
            return new ArrayList<>(newMatrixItems.keySet());
        }

        // find not loaded tile
        List<TileCoordinates> notLoadedTile = new ArrayList<>();
        TileCoordinates currentKey;
        for (Map.Entry<TileCoordinates, GroundOverlayMatrixItem> entry : newMatrixItems.entrySet()) {
            currentKey = entry.getKey();
            if (overlayMatrix.containsKey(currentKey)) {
                entry.setValue(overlayMatrix.get(currentKey));
                overlayMatrix.remove(currentKey);
            } else {
                notLoadedTile.add(entry.getKey());
            }
        }
        GroundOverlayMatrixItem overlayMatrixItem;
        for (Map.Entry<TileCoordinates, GroundOverlayMatrixItem> entry : overlayMatrix.entrySet()) {
            overlayMatrixItem = entry.getValue();
            if (overlayMatrixItem.isOvelayLoaded()) {
                idleOverlay.add(overlayMatrixItem);
            }
        }
        overlayMatrix = newMatrixItems;
        return notLoadedTile;
    }

    private HashMap<TileCoordinates, GroundOverlayMatrixItem> generateMatrixItems (TileCoordinates centerTile) {
        HashMap<TileCoordinates, GroundOverlayMatrixItem> matrix = new HashMap<>();
        HashMap<TileCoordinates, GroundOverlayMatrixItem> rowItems = getRowItems(centerTile);
        for (TileCoordinates tileCoordinates : rowItems.keySet()) {
            matrix.putAll(getColumnItems(tileCoordinates));
        }
        return matrix;
    }

    /**
     * get row items (contain center item)
     * @param center
     * @return
     */
    private HashMap<TileCoordinates, GroundOverlayMatrixItem> getRowItems (TileCoordinates center) {
        HashMap<TileCoordinates, GroundOverlayMatrixItem> matrix = new HashMap<>();
        matrix.put(center, new GroundOverlayMatrixItem());
        try {
            TileCoordinates left = center.getTileLeft();
            TileCoordinates right = center.getTileRight();
            matrix.put(left, new GroundOverlayMatrixItem());
            matrix.put(right, new GroundOverlayMatrixItem());
            matrix.put(left.getTileLeft(), new GroundOverlayMatrixItem());
            matrix.put(right.getTileRight(), new GroundOverlayMatrixItem());
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return matrix;
    }

    /**
     * get column items (contain center item)
     * @param center
     * @return
     */
    private HashMap<TileCoordinates, GroundOverlayMatrixItem> getColumnItems (TileCoordinates center) {
        HashMap<TileCoordinates, GroundOverlayMatrixItem> matrix = new HashMap<>();
        matrix.put(center, new GroundOverlayMatrixItem());
        try {
            TileCoordinates top = center.getTileTop();
            TileCoordinates bot = center.getTileBot();
            matrix.put(top, new GroundOverlayMatrixItem());
            matrix.put(bot, new GroundOverlayMatrixItem());
            matrix.put(top.getTileTop(), new GroundOverlayMatrixItem());
            matrix.put(bot.getTileBot(), new GroundOverlayMatrixItem());
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return matrix;
    }
}
