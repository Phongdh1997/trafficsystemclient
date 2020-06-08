package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class TrafficLoader {
    private ThreadPoolExecutor executor = RetrofitClient.THREAD_POOL_EXECUTOR;
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();
    private TrafficBitmap trafficBitmap;

    public TrafficLoader(GroundOverlayMatrix groundOverlayMatrix, Context context) {
        trafficBitmap = new TrafficBitmap(groundOverlayMatrix, context);
    }

    /**
     * load traffic data from server
     * if success then draw bitmap
     * else ...
     * @param tile
     * @return
     */
    public void loadDataFromServer(final TileCoordinates tile, final HashMap<TileCoordinates, String> tileStates) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tile);
                UserLocation userLocation = new UserLocation(bounds.getCenter());
                Log.e("tile status", tile.toString() + "loading, " + userLocation.toString());
                List<StatusRenderData> datas = statusRepositoryService
                        .loadStatusRenderData(userLocation, getTileRadius(tile));
                trafficBitmap.createTrafficBitmap(tile, StatusRenderData.parseBitmapLineData(datas));
                if (datas == null) {
                    tileStates.put(tile, GroundOverlayMatrix.LOAD_FAIL_OVERLAY);
                }
            }
        });
    }

    /**
     * ref: https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/
     * @param tile
     * @return radius of tile level in meters
     */
    private int getTileRadius (TileCoordinates tile) {
        double resolution = 1;  // meters / pixel
        switch (tile.z) {
            case 13:
                resolution = 19.10925707;
                break;
            case 14:
                resolution = 9.554728536;
                break;
            case 15:
                resolution = 4.777314268;
                break;
            case 16:
                resolution = 2.388657133;
                break;
            case 17:
                resolution = 1.194328566;
                break;
            default:
        }
        double weith = resolution * 256;
        return (int) (Math.sqrt(weith * weith * 2) / 2);
    }
}
