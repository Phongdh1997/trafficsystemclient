package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIServerRetrofitService apiServerRetrofitService;
    private MutableLiveData<GeoJsonLayer> statusRenderGeoJsonLayerLiveData;

    public StatusRemoteRepository() {
        apiServerRetrofitService = RetrofitClient.getApiServerRetrofitService();
        statusRenderGeoJsonLayerLiveData = new MutableLiveData<>();
    }

    public LiveData<GeoJsonLayer> getStatusRenderData() {
        return statusRenderGeoJsonLayerLiveData;
    }

    @Override
    public void loadStatusRenderData(UserLocation userLocation, double zoom, final WeakReference<GoogleMap> mapRef) {
        if (userLocation != null) {
            apiServerRetrofitService.getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                    .enqueue(new Callback<StatusResponse<List<StatusRenderData>>>() {
                        @Override
                        public void onResponse(Call<StatusResponse<List<StatusRenderData>>> call, Response<StatusResponse<List<StatusRenderData>>> response) {
                            GoogleMap googleMap = mapRef.get();
                            if (googleMap == null) return;
                            if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                                try {
                                    JSONObject jsonLayer = StatusRenderData.parseLayerJsonObject(response.body().getData());
                                    GeoJsonLayer geoJsonLayer = new GeoJsonLayer(googleMap, jsonLayer);
                                    statusRenderGeoJsonLayerLiveData.postValue(geoJsonLayer);
                                } catch (Exception e) {}
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse<List<StatusRenderData>>> call, Throwable t) {

                        }
                    });
        }
    }
}
