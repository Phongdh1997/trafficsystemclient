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
import com.hcmut.admin.bktrafficsystem.api.ApiService;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.param.ReportRequest;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.event.CurrentUserLocationEvent;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.SleepWakeupLocationService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.LocationRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.LocationRemoteRepository;
import com.hcmut.admin.bktrafficsystem.ui.MapActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.TrafficNotificationFactory.STOPPED_NOTIFICATION_ID;
import static com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.TrafficNotificationFactory.STOP_LOCATION_SERVICE_ALERT_NOTIFICATION_ID;

public class LocationCollectionManager {

    private static final int INTERVAL = 12000;
    private static final int FASTEST_INTERVAL = 8000;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback callback;
    private static LocationCollectionManager locationCollectionManager;

    private Context context;
    private ApiService apiService;
    private UserLocation lastUserLocation;
    private MutableLiveData<CurrentUserLocationEvent> currentUserLocationEventLiveData;

    private SleepWakeupLocationService sleepWakeupLocationService;

    private LocationCollectionManager(Context context) {
        this.context = context.getApplicationContext();
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context.getApplicationContext());
        currentUserLocationEventLiveData = new MutableLiveData<>();
        apiService = CallApi.createService();
    }

    public void setStopServiceEvent(SleepWakeupLocationService.StopServiceEvent stopServiceEvent) {
        sleepWakeupLocationService.setStopServiceEvent(stopServiceEvent);
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
        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        sleepWakeupLocationService.repare();
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
                postReport(currUserLocation);
                lastUserLocation = currUserLocation;
                sleepWakeupLocationService.handleSleepOrWakeupService(currUserLocation);
            }
        }
    }

    private void postReport(UserLocation currUserLocation) {
        if (lastUserLocation == null) return;
        String accessAuth = MapActivity.currentUser.getAccessToken();
        ReportRequest reportRequest = new ReportRequest(lastUserLocation, currUserLocation);
        apiService.postGPSTrafficReport(accessAuth, reportRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("post GPS report", "code " + response.code());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("post GPS report", "fail");
            }
        });
    }
}
