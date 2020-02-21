package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.hcmut.admin.bktrafficsystem.ui.map.MapDirection;

import java.util.ArrayList;
import java.util.List;

public class ProbeMapUi {

    /**
     * external view
     */
    private AppCompatActivity activity;
    private GoogleMap gmaps;

    private List<Polyline> prevStatusRenderPolylines;
    private Polyline prevDirectionRenderPolyline;
    private MapDirection mapDirection;
    private MapViewModel mapViewModel;

    public ProbeMapUi(@NonNull AppCompatActivity activity, @NonNull GoogleMap map) {
        this.activity = activity;
        this.gmaps = map;

        initUtilsObject();

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

    private void addEvents() {

    }

    private void initUtilsObject() {
        this.mapDirection = new MapDirection();
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
                removePrevStatusRender();
                prevStatusRenderPolylines = renderNewStatus(statusPolylineOptionsList);
            }
        });
        statusRenderEventLiveData.observe(activity, new Observer<StatusRenderEvent>() {
            @Override
            public void onChanged(StatusRenderEvent statusRenderEvent) {
                Log.e("status", "rendering");
                // double zoom = gmaps.getCameraPosition().zoom;
                double zoom = 15.0;
                //UserLocation userLocation = LocationCollectionManager.getInstance(getContext()).getLastUserLocation();
                UserLocation userLocation = new UserLocation(10.776425, 106.663520);
                mapViewModel.rendering(userLocation, zoom);
            }
        });
        directionRenderPolylineOptionsLiveData.observe(activity, new Observer<PolylineOptions>() {
            @Override
            public void onChanged(PolylineOptions polylineOptions) {
                if (polylineOptions != null) {
                    mapDirection.clearDirection();
                    mapDirection.drawDirectPolylines(polylineOptions, gmaps, activity.getApplicationContext());
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

    private void removePrevStatusRender() {
        if (prevStatusRenderPolylines != null) {
            for (Polyline polyline : prevStatusRenderPolylines) {
                polyline.remove();
            }
            prevStatusRenderPolylines.clear();
            prevStatusRenderPolylines = null;
        }
    }

    private List<Polyline> renderNewStatus(List<PolylineOptions> statusPolylineOptionsList) {
        List<Polyline> polylines = new ArrayList<>();
        for (PolylineOptions statusPolylineOptions: statusPolylineOptionsList) {
            polylines.add(gmaps.addPolyline(statusPolylineOptions));
        }
        return polylines;
    }

    public void renderNewDirection(LatLng start, LatLng end) {
        mapViewModel.direct(new UserLocation(start), new UserLocation(end));
    }
}
