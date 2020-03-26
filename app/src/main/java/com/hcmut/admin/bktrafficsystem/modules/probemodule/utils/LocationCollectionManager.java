package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.app.Notification;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Location;
import android.os.HandlerThread;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.LocationRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.LocationRemoteRepository;
import com.hcmut.admin.bktrafficsystem.ui.MapActivity;

public class LocationCollectionManager {

    private static final int INTERVAL = 12000;
    private static final int FASTEST_INTERVAL = 8000;
    private static final int DATA_COLECT_LIMIT = 200;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private HandlerThread locationHandlerThread;
    private LocationCallback callback;
    private static LocationCollectionManager locationCollectionManager;

    private Context context;
    private LocationRepositoryService locationRepositoryService;
    private UserLocation lastUserLocation;
    private MutableLiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData;

    private int stopServiceCountDown = DATA_COLECT_LIMIT;
    private MovingDetection movingDetection = new MovingDetection();

    private LocationCollectionManager(Context context) {
        this.context = context.getApplicationContext();
        locationHandlerThread = new HandlerThread("Location Listener thread");
        locationHandlerThread.start();
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context.getApplicationContext());
        locationRepositoryService = new LocationRemoteRepository();
        currentUserLocationEventLiveData = new MutableLiveData<>();

    }

    public static LocationCollectionManager getInstance(Context context) {
        if (locationCollectionManager == null) {
            locationCollectionManager = new LocationCollectionManager(context);
        }
        return locationCollectionManager;
    }

    public UserLocation getLastUserLocation() {
        return lastUserLocation;
    }

    public void beginTraceLocation() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.callback = new LocationReceiverCallback();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(request, this.callback, locationHandlerThread.getLooper());
        }
    }

    public void endTraceLocation() {
        if (fusedLocationProviderClient != null && callback != null) {
            fusedLocationProviderClient.removeLocationUpdates(callback);
            callback = null;
            fusedLocationProviderClient = null;
        }
        locationHandlerThread.quitSafely();
    }

    public LiveData<CurrentUserLocationEvent> getCurrentUserLocationEventLiveData() {
        return currentUserLocationEventLiveData;
    }

    /**
     * Load current user location and post to live data
     */
    public void loadCurrentLocation(final boolean isMoveToCurrentLocation) {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                                currentUserLocationEventLiveData.postValue(new CurrentUserLocationEvent(userLocation, isMoveToCurrentLocation));
                            } else {
                                currentUserLocationEventLiveData.postValue(new CurrentUserLocationEvent(null));
                            }
                        }
                    });
        }
    }

    public class LocationReceiverCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                UserLocation currUserLocation = new UserLocation(location);
                locationRepositoryService.postLocationRecord(lastUserLocation, currUserLocation); // send location record to server
                lastUserLocation = currUserLocation;
                handleDetectMoving(currUserLocation);
            }
            handleSleepOrWakeupService();
        }
    }

    private void handleDetectMoving(UserLocation currUserLocation) {
        movingDetection.setCurrLocation(currUserLocation);
        movingDetection.isMoving();
    }

    private void handleSleepOrWakeupService() {
        stopServiceCountDown--;
        if (stopServiceCountDown < 1) {
            // notify stop notification
            TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory
                    .getInstance(context);
            Notification notification = trafficNotificationFactory
                    .getStopLocationServiceNotification(
                            context, MapActivity.class);
            trafficNotificationFactory.sendNotification(notification);
            stopServiceCountDown = DATA_COLECT_LIMIT;
        }
    }

}
