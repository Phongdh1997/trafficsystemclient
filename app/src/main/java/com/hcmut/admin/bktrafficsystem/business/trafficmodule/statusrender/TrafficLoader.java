package com.hcmut.admin.bktrafficsystem.business.trafficmodule.statusrender;

import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.repository.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;
import com.hcmut.admin.bktrafficsystem.util.MyLatLngBoundsUtil;

import java.util.List;

@Deprecated
public class TrafficLoader {
    private StatusRepositoryService statusRepositoryService = new StatusRemoteRepository();

    public TrafficLoader() {

    }

    /**
     * load traffic data from server
     * if success then draw bitmap
     * else ...
     * @param tile
     * @return
     */
    public List<StatusRenderData> loadDataFromServer(final TileCoordinates tile) {
        LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tile);
        UserLocation userLocation = new UserLocation(bounds.getCenter());
        Log.e("tile status", tile.toString() + "loading, " + userLocation.toString());
        return statusRepositoryService.loadStatusRenderData(userLocation, getTileRadius(tile));
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
