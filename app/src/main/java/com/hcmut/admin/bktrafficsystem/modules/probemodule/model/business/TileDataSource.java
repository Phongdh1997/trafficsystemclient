package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileDataSource {
    private boolean isTileDataLoaded = false;

    private List<StatusRenderData> statusRenderDataList = new ArrayList<>();

    public void addData(@NotNull List<StatusRenderData> statusRenderDataList) {
        this.statusRenderDataList.addAll(statusRenderDataList);
    }

    public boolean isTileDataLoaded() {
        return isTileDataLoaded;
    }

    public void setTileDataLoaded(boolean state) {
        isTileDataLoaded = state;
    }

    public List<StatusRenderData> getTileDataSource(int x, int y, int zoom) {
        LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(x, y, zoom);
        List<StatusRenderData> temp = new ArrayList<>();
        for (StatusRenderData statusRenderData : statusRenderDataList) {
            if (statusRenderData.isInLatLngBounds(bounds)) {
                temp.add(statusRenderData);
            }
        }
        return temp;
    }
}
