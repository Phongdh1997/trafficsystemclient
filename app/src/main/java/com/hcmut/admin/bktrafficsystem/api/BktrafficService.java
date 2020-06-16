package com.hcmut.admin.bktrafficsystem.api;

import com.hcmut.admin.bktrafficsystem.model.param.FastReport;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface BktrafficService {

    @Headers("Content-Type: application/json")
    @POST("/api/fast-records")
    Call<BaseResponse<Object>> postFastRecord(@Body FastReport fastReport);
}
