package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.HashMap;
import java.util.List;

public abstract class TileRenderHandler {
    private HashMap<TileCoordinates, String> tileStates;

    public TileRenderHandler(HashMap<TileCoordinates, String> tileStates) {
        this.tileStates = tileStates;
    }

    public abstract void render(TileCoordinates tile, List<StatusRenderData> datas, HashMap<TileCoordinates, String> tileStates);

    protected void setTileState(TileCoordinates tile, String state) {
        tileStates.put(tile, state);
    }
}
