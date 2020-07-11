package com.hcmut.admin.bktrafficsystem.repository.remote.API;

import com.hcmut.admin.bktrafficsystem.repository.remote.model.request.RatingBody;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.DirectRespose;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.NearSegmentResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.PatchNotiResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.PostRatingResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.TrafficReportResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.TrafficStatusResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.UserResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.List;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {
    /**
     *
     */
    @GET("/api/traffic-status/get-status")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("lat") Double lat, @Query("lng") Double lng, @Query("zoom") Double zoom);

    /**
     * get traffic status by radius in meters
     */
    @GET("/api/traffic-status/get-status")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("lat") Double lat, @Query("lng") Double lng, @Query("radius_in_meter") int radiusInMeters);

    /**
     * get traffic status by radius in meters
     */
    @GET("/api/traffic-status/get-status?include_user_report=true")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatusIncludeUserReport(
            @Query("lat") Double lat,
            @Query("lng") Double lng,
            @Query("radius_in_meter") int radiusInMeters);

    @GET("api/traffic-status/get-status")
    Call<BaseResponse<List<TrafficStatusResponse>>> getVelocity(@Query("time") long date,
                                                                @Query("segmentId") int segmentId);

    @POST("api/report/segment/here")
    Call<BaseResponse<ReportResponse>> postTrafficReport(@Header("Authorization") String Authorization,
                                                         @Body ReportRequest reportRequest);

    @POST("api/report/segment/here")
    Call<Object> postGPSTrafficReport(@Header("Authorization") String Authorization,
                                      @Body ReportRequest reportRequest);

    @Headers("Content-Type: application/json")
    @POST("api/evaluation/add")
    Call<BaseResponse<PostRatingResponse>> postRating(@Header("Authorization") String Authorization,
                                                      @Body RatingBody ratingBody);

    @GET("api/user/get-user-info")
    Call<BaseResponse<UserResponse>> getUserInfo(@Header("Authorization") String Authorization);

    @POST("api/user/update-user-info")
    @FormUrlEncoded
    Call<BaseResponse<UserResponse>> updateUserInfo(@Header("Authorization") String Authorization,
                                                    @Field("name") String name,
                                                    @Field("email") String email,
                                                    @Field("avatar") String avatar,
                                                    @Field("phone") String phone);

    @POST("api/auth/login-with-facebook")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> loginWithFacebook(@Field("facebook_id") String facebookId,
                                                        @Field("facebook_token") String facebookToken);

    @POST("api/auth/login-with-google")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> loginWithGoogle(@Field("google_id") String googleId,
                                                      @Field("google_token") String googleToken);

    @POST("api/auth/login")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> login(@Field("username") String username,
                                            @Field("password") String password);

    @POST("api/auth/register")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> register(@Field("username") String username,
                                               @Field("password") String password,
                                               @Field("name") String name,
                                               @Field("email") String email,
                                               @Field("phone") String phone);

    @GET("api/segment/find-near")
    Call<BaseResponse<List<NearSegmentResponse>>> getNearSegment(@Query("lat") double lat,
                                                                 @Query("lng") double lng);

    @GET("api/report/segment/current-reports")
    Call<BaseResponse<List<TrafficReportResponse>>> getCurrentTrafficReport(@Query("lat") double lat,
                                                                            @Query("lng") double lng);

    @GET("api/report/segment/report-detail")
    Call<BaseResponse<ReportResponse>> getDetailTrafficReport(@Query("id") String id);

    @GET("api/report/segment/reports")
    Call<BaseResponse<List<ReportResponse>>> getReportOfTrafficStatus(@Query("time") Long time,
                                                                      @Query("segmentId") int segmentId);

    @FormUrlEncoded
    @POST("api/notification/update-current-location")
    Call<BaseResponse<PatchNotiResponse>> patchUserNoti(@Header("Authorization") String Authorization,
                                                        @Field("token") String notiToken,
                                                        @Field("lat") Double currentLat,
                                                        @Field("lng") Double currentLng,
                                                        @Field("active") String active,
                                                        @Nullable @Field("path_id") String pathId);

    @GET("api/segment/direct")
    Call<BaseResponse<List<DirectRespose>>> getFindDirect(@Query("slat") double slat,
                                                          @Query("slng") double slng,
                                                          @Query("elat") double elat,
                                                          @Query("elng") double elng,
                                                          @Query("type") String type);

    @Multipart
    @POST("api/file/upload")
    Call<BaseResponse<String>> uploadFile(@Part("file") RequestBody file);
}
