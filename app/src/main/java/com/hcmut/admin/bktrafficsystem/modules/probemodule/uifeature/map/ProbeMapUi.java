package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProbeMapUi {

    private static final float MAP_LOAD_RANGE = 500; // meter

    /**
     * external view
     */
    private AppCompatActivity activity;
    private GoogleMap gmaps;

    private List<Polyline> prevStatusRenderPolylines;
    private Polyline prevDirectionRenderPolyline;
    private MapViewModel mapViewModel;

    private UserLocation lastCameraTarget;

    public ProbeMapUi(@NonNull AppCompatActivity activity, @NonNull GoogleMap map) {
        this.activity = activity;
        this.gmaps = map;

        getViewModel();
        addViewModelObserver();
        addEvents();
    }

    public void startStatusRenderTimer() {
        mapViewModel.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer() {
        mapViewModel.stopStatusRenderTimer();
    }

    public void render() {
        mapViewModel.triggerRender();
    }

    private void addEvents() {

    }

    private void getViewModel() {
        mapViewModel = ViewModelProviders.of(activity).get(MapViewModel.class);
    }

    private void addViewModelObserver() {
        // get live data
        LiveData<List<PolylineOptions>> statusPolylineOptionsLiveData = mapViewModel.getStatusRenderPolylineOptionsLiveData();
        LiveData<StatusRenderEvent> statusRenderEventLiveData = mapViewModel.getStatusRenderEventLiveData();
        LiveData<PolylineOptions> directionRenderPolylineOptionsLiveData = mapViewModel.getDirectionRenderPolylineOptionsLiveData();
        LiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData = mapViewModel.getCurrentUserLocationEventLiveData();

        // set observer
        statusPolylineOptionsLiveData.observe(activity, new Observer<List<PolylineOptions>>() {
            @Override
            public void onChanged(List<PolylineOptions> statusPolylineOptionsList) {
                renderStatus(statusPolylineOptionsList);
            }
        });
        statusRenderEventLiveData.observe(activity, new Observer<StatusRenderEvent>() {
            @Override
            public void onChanged(StatusRenderEvent statusRenderEvent) {
                // render status at current camera
                double zoom = gmaps.getCameraPosition().zoom;
                if (zoom < 15 || zoom > 21) return;
                UserLocation currentCameraTarget = new UserLocation(gmaps.getCameraPosition().target);
                if (lastCameraTarget == null ||
                        currentCameraTarget.distanceTo(lastCameraTarget) > MAP_LOAD_RANGE) {
                    Log.e("render at", "camera target " + currentCameraTarget.toString());
                    mapViewModel.rendering(currentCameraTarget, zoom);
                    lastCameraTarget = currentCameraTarget;
                }
            }
        });
        directionRenderPolylineOptionsLiveData.observe(activity, new Observer<PolylineOptions>() {
            @Override
            public void onChanged(PolylineOptions polylineOptions) {
                if (polylineOptions != null) {
                    removePrevDirectionRender();
                    prevDirectionRenderPolyline = gmaps.addPolyline(polylineOptions);
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Tìm đường thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        currentUserLocationEventLiveData.observe(activity, new Observer<CurrentUserLocationEvent>() {
            @Override
            public void onChanged(CurrentUserLocationEvent currentUserLocationEvent) {
                UserLocation currentUserLocation = currentUserLocationEvent.getCurrentUserLocation();
                if ( currentUserLocation != null) {
                    // mark current location on the map
                    LatLng sydney = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                    gmaps.addMarker(new MarkerOptions().position(sydney).title("Test Render"));

                    if (currentUserLocationEvent.isMoveToCurrentLocation()){
                        gmaps.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Không thể lấy được vị trí", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removePrevDirectionRender() {
        if (prevDirectionRenderPolyline != null) {
            prevDirectionRenderPolyline.remove();
            prevDirectionRenderPolyline = null;
        }
    }

    @Deprecated
    private void removePrevStatusRender() {
        if (prevStatusRenderPolylines != null) {
            for (Polyline polyline : prevStatusRenderPolylines) {
                polyline.remove();
            }
            prevStatusRenderPolylines.clear();
            prevStatusRenderPolylines = null;
        }
    }

    @Deprecated
    private List<Polyline> renderNewStatus(List<PolylineOptions> statusPolylineOptionsList) {
        List<Polyline> polylines = new ArrayList<>();
        for (PolylineOptions statusPolylineOptions: statusPolylineOptionsList) {
            polylines.add(gmaps.addPolyline(statusPolylineOptions));
        }
        return polylines;
    }

    private void renderStatus(List<PolylineOptions> statusPolylineOptionsList){
        for (PolylineOptions statusPolylineOptions: statusPolylineOptionsList) {
            gmaps.addPolyline(statusPolylineOptions);
        }
        Log.e("render", "render " + statusPolylineOptionsList.size() + " polyline");
    }
}
