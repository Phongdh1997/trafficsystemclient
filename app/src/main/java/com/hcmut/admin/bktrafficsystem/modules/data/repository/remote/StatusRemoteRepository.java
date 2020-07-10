package com.hcmut.admin.bktrafficsystem.modules.data.repository.remote;

import com.hcmut.admin.bktrafficsystem.modules.data.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.data.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIServerRetrofitService apiServerRetrofitService;

    public StatusRemoteRepository() {
        apiServerRetrofitService = RetrofitClient.getApiServerRetrofitService();
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom) {
        if (userLocation != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiServerRetrofitService
                        .getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                        .execute();
                return response.body().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters) {
        if (userLocation != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiServerRetrofitService
                        .getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), radiusInMeters)
                        .execute();
                return response.body().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
