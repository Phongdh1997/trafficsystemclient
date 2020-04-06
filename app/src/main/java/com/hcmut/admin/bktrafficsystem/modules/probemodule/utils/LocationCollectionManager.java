package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.app.Notification;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Location;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

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
    private static final int STOP_MAX_TIMES = 5;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback callback;
    private static LocationCollectionManager locationCollectionManager;

    private Context context;
    private LocationRepositoryService locationRepositoryService;
    private UserLocation lastUserLocation;
    private MutableLiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData;

    private int stopServiceCountDown = DATA_COLECT_LIMIT;
    private int stopCount = 0;
    private MovingDetection movingDetection = new MovingDetection();
    private MutableLiveData<Boolean> movingStateLiveData = new MutableLiveData<>();

    private StopServiceEvent stopServiceEvent;

    private LocationCollectionManager(Context context) {
        this.context = context.getApplicationContext();
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context.getApplicationContext());
        locationRepositoryService = new LocationRemoteRepository();
        currentUserLocationEventLiveData = new MutableLiveData<>();

    }

    public LiveData<Boolean> getMovingStateLiveData () {
        return movingStateLiveData;
    }

    public void setStopServiceEvent(StopServiceEvent stopServiceEvent) {
        this.stopServiceEvent = stopServiceEvent;
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

    public void beginTraceLocation(Looper looper) {
        stopServiceCountDown = DATA_COLECT_LIMIT;
        stopCount = 0;
        movingDetection = new MovingDetection();
        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.callback = new LocationReceiverCallback();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(request, this.callback, looper);
        }
    }

    public void endTraceLocation() {
        if (fusedLocationProviderClient != null && callback != null) {
            fusedLocationProviderClient.removeLocationUpdates(callback);
            callback = null;
        }
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
        if (!movingDetection.isMoving()) {
            stopCount++;
        } else {
            stopCount = 0;
        }
        Log.e("stoping", "stop count: " + stopCount);

        // nếu người dùng không di chuyển ra khỏi vị trí trong
        // STOP_MAX_TIMES lần lấy tọa độ, thì xác định người dùng không di chuyển
        if (stopCount > STOP_MAX_TIMES) {
            // user is not move
            // stop service
            movingStateLiveData.postValue(false);
            if (stopServiceEvent != null) {
                stopServiceEvent.onStop();
            }
            stopCount = 0;
        }
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

    public interface StopServiceEvent {
        void onStop();
    }
}
