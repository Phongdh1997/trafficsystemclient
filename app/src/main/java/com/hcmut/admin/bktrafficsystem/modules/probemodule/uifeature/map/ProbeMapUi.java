package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.model.user.User;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ProbeMapUi {

    /**
     * external view
     */
    private AppCompatActivity activity;
    private GoogleMap gmaps;

    private Polyline prevDirectionRenderPolyline;
    private MapViewModel mapViewModel;
    private StatusOverlayRender statusOverlayRender;

    private Executor executor = AsyncTask.SERIAL_EXECUTOR;
    private ArrayList<LatLng> renderedCameraTargets = new ArrayList<>();

    public ProbeMapUi(@NonNull AppCompatActivity activity, @NonNull GoogleMap map) {
        this.activity = activity;
        this.gmaps = map;
        statusOverlayRender = new StatusOverlayRender(map);

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

    public void onMapMoved() {
        LatLng currCameraTarget = gmaps.getCameraPosition().target;
        float zoom = gmaps.getCameraPosition().zoom;
        if (zoom < 15.0f || zoom > 21.0f) return;
        if (renderedCameraTargets.size() < 1) {
            renderedCameraTargets.add(currCameraTarget);
            mapViewModel.triggerRender(currCameraTarget, zoom);
        } else {
            if (!isCameraTargetsInsideVisibleRegion(renderedCameraTargets)) {
                renderedCameraTargets.add(currCameraTarget);
                mapViewModel.triggerRender(currCameraTarget, zoom);
                Log.e("camera", "outside screen");
            } else {
                Log.e("camera", "inside screen");
            }
        }

    }

    private boolean isCameraTargetsInsideVisibleRegion(ArrayList<LatLng> renderedCameraTargets) {
        LatLngBounds bounds = gmaps.getProjection().getVisibleRegion().latLngBounds;
        for (LatLng cameraTarget : renderedCameraTargets) {
            if (bounds.contains(cameraTarget)) {
                return true;
            }
        }
        return false;
    }

    private void addEvents() {

    }

    private void getViewModel() {
        mapViewModel = ViewModelProviders.of(activity).get(MapViewModel.class);
    }

    private void addViewModelObserver() {
        mapViewModel.setTileOverlayRender(statusOverlayRender);

        // get live data
        LiveData<StatusRenderEvent> statusRenderEventLiveData = mapViewModel.getStatusRenderEventLiveData();
        LiveData<PolylineOptions> directionRenderPolylineOptionsLiveData = mapViewModel.getDirectionRenderPolylineOptionsLiveData();
        LiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData = mapViewModel.getCurrentUserLocationEventLiveData();

        // set observer
        statusRenderEventLiveData.observe(activity, new Observer<StatusRenderEvent>() {
            @Override
            public void onChanged(StatusRenderEvent statusRenderEvent) {
                double zoom;
                UserLocation currentCameraTarget;
                if (statusRenderEvent.isHaveCameraTarget()) {
                    zoom = statusRenderEvent.getZoom();
                    currentCameraTarget = new UserLocation(statusRenderEvent.getCameraTarget());
                } else {
                    zoom = gmaps.getCameraPosition().zoom;
                    currentCameraTarget = new UserLocation(gmaps.getCameraPosition().target);
                }
                if (zoom > 15.0f && zoom < 21.0f) {
                    mapViewModel.rendering(currentCameraTarget, zoom);
                }
                if (statusRenderEvent.isClearRender()) {
                    clearTileOverlayRender();
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

    private void clearTileOverlayRender() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                statusOverlayRender.clearRender();
            }
        });
    }

    private void removePrevDirectionRender() {
        if (prevDirectionRenderPolyline != null) {
            prevDirectionRenderPolyline.remove();
            prevDirectionRenderPolyline = null;
        }
    }
}
