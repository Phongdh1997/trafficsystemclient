package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIServerRetrofitService apiServerRetrofitService;

    private StatusOverlayRender statusOverlayRender;

    public StatusRemoteRepository() {
        apiServerRetrofitService = RetrofitClient.getApiServerRetrofitService();
    }

    @Override
    public void setStatusOverlayRender(StatusOverlayRender statusOverlayRender) {
        this.statusOverlayRender = statusOverlayRender;
    }

    @Override
    public void loadStatusRenderData(UserLocation userLocation, double zoom) {
        if (userLocation != null) {
            apiServerRetrofitService.getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                    .enqueue(new Callback<StatusResponse<List<StatusRenderData>>>() {
                        @Override
                        public void onResponse(Call<StatusResponse<List<StatusRenderData>>> call, Response<StatusResponse<List<StatusRenderData>>> response) {
                            if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                                if (statusOverlayRender != null) {
                                    statusOverlayRender.setDataSource(response.body().getData());
                                    statusOverlayRender.notifyDataChange();
                                } else {
                                    Log.e("status overlay render", "Null");
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
