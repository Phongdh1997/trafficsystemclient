package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.business;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.DirectionRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.DirectionRemoteRepository;

public class DirectionRender {

    private LiveData<PolylineOptions> directionRenderPolylineOptionsLiveData;

    private DirectionRepositoryService directionRepositoryService;

    public DirectionRender() {
        directionRepositoryService = new DirectionRemoteRepository();
        directionRenderPolylineOptionsLiveData = directionRepositoryService.getDirectionRenderData();
    }

    public LiveData<PolylineOptions> getDirectionRenderData() {
        return directionRenderPolylineOptionsLiveData;
    }

    public void direct(UserLocation startPoint, UserLocation endPoint) {
        directionRepositoryService.loadDirectionRenderData(startPoint, endPoint);
    }
}
