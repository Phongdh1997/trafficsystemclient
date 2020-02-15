package com.hcmut.admin.googlemapapitest.modules.probemodule.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.hcmut.admin.googlemapapitest.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.googlemapapitest.modules.probemodule.utils.LocationCollectionManager;

public class MainViewModel extends AndroidViewModel {

    private LocationCollectionManager locationCollectionManager;

    public MainViewModel(@NonNull Application application) {
        super(application);
        locationCollectionManager = LocationCollectionManager.getInstance(application.getApplicationContext());
    }

    /**
     * View action: load current location
     */
    public void moveToCurrentLocation() {
        locationCollectionManager.loadCurrentLocation(CurrentUserLocationEvent.DO_NOT_MOVE_TO_CURRENT_LOCATION);
    }
}
