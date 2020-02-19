package com.hcmut.admin.bktrafficsystem.modules.probemodule.event;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

public class CurrentUserLocationEvent {
    public static final boolean MOVE_TO_CURRENT_LOCATION = true;
    public static final boolean DO_NOT_MOVE_TO_CURRENT_LOCATION = false;

    private UserLocation currentUserLocation;
    private boolean isMoveToCurrentLocation;

    public CurrentUserLocationEvent(UserLocation currentUserLocation, boolean isMoveToCurrentLocation) {
        this.currentUserLocation = currentUserLocation;
        this.isMoveToCurrentLocation = isMoveToCurrentLocation;
    }

    public CurrentUserLocationEvent(UserLocation currentUserLocation) {
        this.currentUserLocation = currentUserLocation;
        this.isMoveToCurrentLocation = false;
    }

    public UserLocation getCurrentUserLocation() {
        return currentUserLocation;
    }

    public boolean isMoveToCurrentLocation() {
        return isMoveToCurrentLocation;
    }
}
