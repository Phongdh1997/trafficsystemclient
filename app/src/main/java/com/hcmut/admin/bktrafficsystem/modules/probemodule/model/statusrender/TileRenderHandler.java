package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tileoverlay.LoadedTileManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class TileRenderHandler {
    protected LoadedTileManager loadedTileManager;
    public static Handler mainHandler = new Handler(Looper.getMainLooper());

    public TileRenderHandler() {
        loadedTileManager = LoadedTileManager.getInstance();
    }

    public abstract <T> Bitmap render(TileCoordinates tile, List<T> datas);

    public abstract void render(TileCoordinates tile, Bitmap bitmap);

    public abstract void clearRender ();

    protected void tileLoaded(TileCoordinates tile) {
        loadedTileManager.setLoadedTile(tile);
    }

    protected void tileLoading(TileCoordinates tile) {
        loadedTileManager.setLoadingTile(tile);
    }

    protected void tileLoadFail(TileCoordinates tile) {
        loadedTileManager.setLoadFailTile(tile);
    }

    protected void runOnUiThread(@NotNull Runnable runnable) {
        mainHandler.post(runnable);
    }
}
