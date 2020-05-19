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
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.MoveToLoadHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business.TileDataSource;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.MyLatLngBoundsUtil;
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
    private MoveToLoadHandler moveToLoadHandler;

    private Executor executor = AsyncTask.SERIAL_EXECUTOR;
    private ArrayList<LatLng> renderedCameraTargets = new ArrayList<>();

    LatLngBounds bounds;

    public ProbeMapUi(@NonNull AppCompatActivity activity, @NonNull GoogleMap map) {
        this.activity = activity;
        this.gmaps = map;

        TileDataSource tileDataSource = new TileDataSource();
        statusOverlayRender = new StatusOverlayRender(map, tileDataSource);


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

    public void onCameraMoved() {
        moveToLoadHandler.loadTrafficStatus(gmaps);
    }

    private void addEvents() {

    }

    private void getViewModel() {
        mapViewModel = ViewModelProviders.of(activity).get(MapViewModel.class);
    }

    private void addViewModelObserver() {
        // get live data
        LiveData<StatusRenderEvent> statusRenderEventLiveData = mapViewModel.getStatusRenderEventLiveData();
        LiveData<PolylineOptions> directionRenderPolylineOptionsLiveData = mapViewModel.getDirectionRenderPolylineOptionsLiveData();
        LiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData = mapViewModel.getCurrentUserLocationEventLiveData();

        // set observer
        statusRenderEventLiveData.observe(activity, new Observer<StatusRenderEvent>() {
            @Override
            public void onChanged(StatusRenderEvent statusRenderEvent) {
                clearTileOverlayRender();
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
        statusOverlayRender.notifyDataChange();
    }

    private void removePrevDirectionRender() {
        if (prevDirectionRenderPolyline != null) {
            prevDirectionRenderPolyline.remove();
            prevDirectionRenderPolyline = null;
        }
    }
}
