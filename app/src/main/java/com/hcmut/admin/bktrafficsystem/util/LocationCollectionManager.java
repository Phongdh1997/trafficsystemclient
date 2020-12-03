package com.hcmut.admin.bktrafficsystem.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.bktrafficsystem.business.SleepWakeupLocationService;
import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationCollectionManager {

    private static final int INTERVAL = 12000;
    private static final int FASTEST_INTERVAL = 8000;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback callback;
    private static LocationCollectionManager locationCollectionManager;

    private Context context;
    private APIService apiService;
    private UserLocation lastUserLocation;

    private SleepWakeupLocationService sleepWakeupLocationService;

    private LocationCollectionManager(Context context) {
        this.context = context.getApplicationContext();
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context.getApplicationContext());
        apiService = RetrofitClient.getApiService();
        sleepWakeupLocationService = new SleepWakeupLocationService(context);
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

    public void getCurrentLocation(OnSuccessListener<Location> onSuccessListener) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(onSuccessListener);
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

    public class LocationReceiverCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                UserLocation currUserLocation = new UserLocation(location);
                postReport(currUserLocation);
                lastUserLocation = currUserLocation;
                //sleepWakeupLocationService.handleSleepOrWakeupService(currUserLocation);
            }
        }
    }

    private void postReport(UserLocation currUserLocation) {
        if (lastUserLocation == null) return;
        String accessAuth = MapActivity.currentUser.getAccessToken();
        ReportRequest reportRequest = new ReportRequest(lastUserLocation, currUserLocation);
        reportRequest.checkUpdateCurrentLocation(context);
        apiService.postGPSTrafficReport(accessAuth, reportRequest).enqueue(new Callback<BaseResponse<ReportResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                Log.e("post GPS report", response.toString());
            }

            @Override
            public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                Log.e("post GPS report", "fail");
            }
        });
    }

    public boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * query to stop notification to server
     */
    public void stopNotification() {
        getCurrentLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null && lastUserLocation == null) {
                    return;
                }
                UserLocation userLocation;
                if (location != null) {
                    userLocation = new UserLocation(location);
                } else {
                    userLocation = lastUserLocation;
                }
                String accessAuth = MapActivity.currentUser.getAccessToken();
                ReportRequest reportRequest = ReportRequest.getStopNotificationModel(userLocation, context);
                apiService.postGPSTrafficReport(accessAuth, reportRequest).enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                        Log.e("post GPS report", response.toString());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        Log.e("post GPS report", "fail");
                    }
                });
            }
        });
    }
}
