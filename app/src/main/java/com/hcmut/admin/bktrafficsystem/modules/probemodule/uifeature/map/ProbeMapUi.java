package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.MoveToLoad;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.BitmapTileRenderHandler;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileBitmapRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.viewmodel.MapViewModel;

import org.jetbrains.annotations.NotNull;

public class ProbeMapUi {

    /**
     * external view
     */
    private AppCompatActivity activity;
    private GoogleMap gmaps;

    private Polyline prevDirectionRenderPolyline;
    private MapViewModel mapViewModel;
    private MoveToLoad moveToLoad;

    public ProbeMapUi(@NonNull AppCompatActivity activity, @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.activity = activity;
        this.gmaps = map;
        moveToLoad = new MoveToLoad(gmaps, activity.getApplicationContext(), mapFragment);

        getViewModel();
        addViewModelObserver();
        addEvents();
        setupGmap();
    }

    public void startStatusRenderTimer() {
        mapViewModel.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer() {
        mapViewModel.stopStatusRenderTimer();
    }

    public void setupGmap () {
        gmaps.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                moveToLoad.cameraMove(gmaps);
            }
        });

        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                moveToLoad.cameraMove(gmaps);
            }
        });
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
}
