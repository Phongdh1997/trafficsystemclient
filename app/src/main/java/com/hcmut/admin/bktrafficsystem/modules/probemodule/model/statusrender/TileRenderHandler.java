package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class TileRenderHandler {
    protected HashMap<TileCoordinates, String> tileStates;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public TileRenderHandler(HashMap<TileCoordinates, String> tileStates) {
        this.tileStates = tileStates;
    }

    public abstract Bitmap render(TileCoordinates tile, List<StatusRenderData> datas, HashMap<TileCoordinates, String> tileStates);

    public abstract void render(TileCoordinates tile, Bitmap bitmap, HashMap<TileCoordinates, String> tileStates);

    public abstract void clearRender ();

    protected void setTileState(TileCoordinates tile, String state) {
        tileStates.put(tile, state);
    }

    protected void runOnUiThread(@NotNull Runnable runnable) {
        mainHandler.post(runnable);
    }
}
