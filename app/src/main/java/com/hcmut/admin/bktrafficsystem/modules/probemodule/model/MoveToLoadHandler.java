package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business.TileDataSource;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class MoveToLoadHandler {
    private LatLng lastCameraTarget;

    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private TileDataSource tileDataSource;
    private ThreadPoolExecutor executor = RetrofitClient.THREAD_POOL_EXECUTOR;

    private boolean isLoading = false;

    public MoveToLoadHandler(TileDataSource tileDataSource) {
        this.tileDataSource = tileDataSource;
    }

    public void loadTrafficStatus(GoogleMap mMap) {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        float zoom = mMap.getCameraPosition().zoom;
        if (zoom < 15 || zoom > 21) {
            return;
        }
        LatLng currentCameraTarget = bounds.getCenter();
        if (lastCameraTarget == null) {
            //loadMoreStatus(currentCameraTarget, zoom);
            //lastCameraTarget = currentCameraTarget;
        } else if (!bounds.contains(lastCameraTarget)) {    // camera move outside lastCameraTarget
            Log.e("camera", "outside screen");

        }
    }

    private void loadMoreStatus(final LatLng currentTarget, final float zoom) {
        if (!isLoading) {
            isLoading = true;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<StatusRenderData> statusRenderDataList = statusRepositoryService.loadStatusRenderData(new UserLocation(currentTarget), zoom);
                    if (statusRenderDataList != null && statusRenderDataList.size() > 0) {
                        lastCameraTarget = currentTarget;
                        tileDataSource.addData(statusRenderDataList);
                    }
                    isLoading = false;
                }
            });
        }
    }

    private static class TileBoundingCheck {
        private LatLng northeast;
        private LatLng southwest;

        private TileBoundingCheck(LatLngBounds bounds) {
            northeast = bounds.northeast;
            southwest = bounds.southwest;
        }
    }
}
