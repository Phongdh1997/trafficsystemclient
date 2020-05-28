package com.hcmut.admin.bktrafficsystem.modules.probemodule.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.StatusRenderEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business.DirectionRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business.StatusRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.LocationCollectionManager;

public class MapViewModel extends AndroidViewModel {
    private LiveData<StatusRenderEvent> statusRenderEventLiveData;
    private LiveData<PolylineOptions> directionRenderPolylineOptionsLiveData;
    private LiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData;

    private StatusRender statusRender;
    private DirectionRender directionRender;
    private LocationCollectionManager locationCollectionManager;

    public MapViewModel(@NonNull Application application) {
        super(application);
        statusRender = new StatusRender();
        directionRender = new DirectionRender();
        statusRenderEventLiveData = statusRender.getStatusRenderEvent();
        directionRenderPolylineOptionsLiveData = directionRender.getDirectionRenderData();
        locationCollectionManager = LocationCollectionManager.getInstance(application.getApplicationContext());
        currentUserLocationEventLiveData = locationCollectionManager.getCurrentUserLocationEventLiveData();
    }

    public LiveData<StatusRenderEvent> getStatusRenderEventLiveData() {
        return statusRenderEventLiveData;
    }

    public LiveData<PolylineOptions> getDirectionRenderPolylineOptionsLiveData() {
        return directionRenderPolylineOptionsLiveData;
    }

    public LiveData<CurrentUserLocationEvent> getCurrentUserLocationEventLiveData() {
        return currentUserLocationEventLiveData;
    }

    /**
     * View action
     */
    public void startStatusRenderTimer() {
        statusRender.startStatusRenderTimer();
    }

    /**
     * View action
     */
    public void stopStatusRenderTimer () {
        statusRender.stopStatusRenderTimer();
    }

    /**
     * View action
     */
    public void direct(UserLocation startPoint, UserLocation endPoint) {
        directionRender.direct(startPoint, endPoint);
    }
}
