package com.hcmut.admin.bktrafficsystem.repository;

import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.hcmut.admin.bktrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.List;

import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIService apiService;

    public StatusRemoteRepository() {
        apiService = RetrofitClient.getApiService();
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom) {
        if (userLocation != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiService
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
                Response<StatusResponse<List<StatusRenderData>>> response = apiService
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
