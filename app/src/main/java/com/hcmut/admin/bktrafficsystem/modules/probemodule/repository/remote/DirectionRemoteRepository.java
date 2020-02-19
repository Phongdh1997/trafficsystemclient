package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.PolylineOptions;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.DirectionRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.SearchWayData;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionRemoteRepository implements DirectionRepositoryService {

    private static final String DISTANCE_TYPE = "distance";

    private APIServerRetrofitService apiServerRetrofitService;
    private MutableLiveData<PolylineOptions> directionRenderPolylineOptionsLiveData;

    public DirectionRemoteRepository() {
        apiServerRetrofitService = RetrofitClient.getApiServerRetrofitService();
        directionRenderPolylineOptionsLiveData = new MutableLiveData<>();
    }

    public LiveData<PolylineOptions> getDirectionRenderData() {
        return directionRenderPolylineOptionsLiveData;
    }

    @Override
    public void loadDirectionRenderData(UserLocation startPoint, UserLocation endPoint) {
        if (startPoint != null && endPoint != null) {
            apiServerRetrofitService.getWays(
                    startPoint.getLatitude(),
                    startPoint.getLongitude(),
                    endPoint.getLatitude(),
                    endPoint.getLongitude(),
                    DISTANCE_TYPE)
                    .enqueue(new Callback<StatusResponse<List<SearchWayData>>>() {
                        @Override
                        public void onResponse(Call<StatusResponse<List<SearchWayData>>> call, Response<StatusResponse<List<SearchWayData>>> response) {
                            try {
                                List<SearchWayData> searchWayDatas = response.body().getData();
                                PolylineOptions directionPolylineOption = SearchWayData.parsePolylineOptions(searchWayDatas.get(0));
                                directionRenderPolylineOptionsLiveData.postValue(directionPolylineOption);
                            } catch (Exception e) {
                                directionRenderPolylineOptionsLiveData.postValue(null);
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse<List<SearchWayData>>> call, Throwable t) {
                            t.printStackTrace();
                            directionRenderPolylineOptionsLiveData.postValue(null);
                        }
                    });

        }
    }
}
