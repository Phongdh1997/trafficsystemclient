package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.LocationRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.ProbeServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.request.LocationRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRemoteRepository implements LocationRepositoryService {

    private ProbeServerRetrofitService probeServerRetrofitService;

    public LocationRemoteRepository() {
        probeServerRetrofitService = RetrofitClient.getProbeServerRetrofitService();
    }

    @Override
    public void postLocationRecord(UserLocation prevUserLocation, UserLocation currUserLocation) {
        if (prevUserLocation == null || currUserLocation == null) return;
        LocationRecord requestBody = new LocationRecord(prevUserLocation, currUserLocation);
        probeServerRetrofitService.postNewLocationRecord(requestBody)
                .enqueue(new Callback<StatusResponse<StatusResponse.EmptyData>>() {
                    @Override
                    public void onResponse(Call<StatusResponse<StatusResponse.EmptyData>> call, Response<StatusResponse<StatusResponse.EmptyData>> response) {
                        try {
                            Log.e("post location", response.body().toString());
                        } catch (Exception e) {
                            Log.e("post location", "fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse<StatusResponse.EmptyData>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
