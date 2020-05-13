package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIServerRetrofitService apiServerRetrofitService;
    private MutableLiveData<List<PolylineOptions>> statusRenderPolylineOptionsLiveData;

    public StatusRemoteRepository() {
        apiServerRetrofitService = RetrofitClient.getApiServerRetrofitService();
        statusRenderPolylineOptionsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<PolylineOptions>> getStatusRenderData() {
        return statusRenderPolylineOptionsLiveData;
    }

    @Override
    public void loadStatusRenderData(UserLocation userLocation, double zoom) {
        if (userLocation != null) {
            apiServerRetrofitService.getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                    .enqueue(new Callback<StatusResponse<List<StatusRenderData>>>() {
                        @Override
                        public void onResponse(Call<StatusResponse<List<StatusRenderData>>> call, Response<StatusResponse<List<StatusRenderData>>> response) {
                            if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                                List<PolylineOptions> polylineOptions = StatusRenderData.parsePolylineOptions(response.body().getData());
                                if (polylineOptions != null) {
                                    statusRenderPolylineOptionsLiveData.postValue(polylineOptions);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse<List<StatusRenderData>>> call, Throwable t) {

                        }
                    });
        }
    }
}
