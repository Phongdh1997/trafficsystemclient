package com.hcmut.admin.googlemapapitest.modules.probemodule.repository.remote.retrofit.API;

import com.hcmut.admin.googlemapapitest.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.googlemapapitest.modules.probemodule.repository.remote.retrofit.model.response.SearchWayData;
import com.hcmut.admin.googlemapapitest.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIServerRetrofitService {
    /**
     *
     */
    @GET("/api/segment/direct")
    Call<StatusResponse<List<SearchWayData>>> getWays(@Query("slat") double startLatitude,
                                                      @Query("slng") double startLongitude,
                                                      @Query("elat") double endLatitude,
                                                      @Query("elng") double endLongitude,
                                                      @Query("type") String type);

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZGMzZTcxMWE2ZGE5YTNiMmNlYmMxYmIiLCJ1c2VybmFtZSI6ImtoYW5odHJhbiIsImlhdCI6MTU3MzExOTc3Nn0.pk7-o3cj5y9UuqlAYL2mSJzPa_fazfrlJPLMTFF1AvA")
    @FormUrlEncoded
    @POST("/api/notification/update-current-location")
    Call<StatusResponse<Object>> registerNotificationToServer(@Field("token") String token,
                                                              @Field("lat") String lat,
                                                              @Field("lng") String lng,
                                                              @Field("active") String active,
                                                              @Field("path_id") String pathId,
                                                              @Field("segment_id") String nearSegmentId);

    /**
     *
     */
    @GET("/api/traffic-status/get-status")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("lat") Double lat, @Query("lng") Double lng, @Query("zoom") Double zoom);
}
