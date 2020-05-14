package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

    public void render() {
        mapViewModel.triggerRender();
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
                // render status at current camera
                double zoom = gmaps.getCameraPosition().zoom;
                if (zoom < 15 || zoom > 21) return;
                UserLocation currentCameraTarget = new UserLocation(gmaps.getCameraPosition().target);
                if (statusRenderEvent.isClearRender()) {
                    clearTileOverlayRender();
                }
                mapViewModel.rendering(currentCameraTarget, zoom);
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
