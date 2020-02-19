package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.request.LocationRecord;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProbeServerRetrofitService {
    /**
     *
     */
    @POST("/add-location-record")
    Call<StatusResponse<StatusResponse.EmptyData>> postNewLocationRecord(@Body LocationRecord locationRecord);
}
