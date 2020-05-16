package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.io.IOException;
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
                Log.e("userlocation", "" + userLocation.getLatitude() + ", " + userLocation.getLongitude());
                Response<StatusResponse<List<StatusRenderData>>> response = apiServerRetrofitService
                        .getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                        .execute();
                return response.body().getData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
