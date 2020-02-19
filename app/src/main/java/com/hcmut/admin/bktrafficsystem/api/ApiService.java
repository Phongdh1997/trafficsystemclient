package com.hcmut.admin.bktrafficsystem.api;

import com.hcmut.admin.bktrafficsystem.model.param.ReportRequest;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.DirectRespose;
import com.hcmut.admin.bktrafficsystem.model.response.LoginResponse;
import com.hcmut.admin.bktrafficsystem.model.response.NearSegmentResponse;
import com.hcmut.admin.bktrafficsystem.model.response.PatchNotiResponse;
import com.hcmut.admin.bktrafficsystem.model.response.PostRatingResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.model.response.TrafficReportResponse;
import com.hcmut.admin.bktrafficsystem.model.response.TrafficStatusResponse;
import com.hcmut.admin.bktrafficsystem.model.response.UserResponse;

import java.util.List;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/traffic-status/get-status")
    Call<BaseResponse<List<TrafficStatusResponse>>> getTrafficStatus(@Query("lat") double lat,
                                                                     @Query("lng") double lng,
                                                                     @Query("zoom") int zoom);

    @GET("api/traffic-status/get-status")
    Call<BaseResponse<List<TrafficStatusResponse>>> getVelocity(@Query("time") long date,
                                                                @Query("segmentId") int segmentId);

    @POST("api/report/segment/here")
    Call<BaseResponse<ReportResponse>> postTrafficReport(@Header("Authorization") String Authorization,
                                                         @Body ReportRequest reportRequest);

    @POST("api/evaluation/add")
    @FormUrlEncoded
    Call<BaseResponse<PostRatingResponse>> postRating(@Header("Authorization") String Authorization,
                                                      @Field("report") String reportId,
                                                      @Field("score") float score);

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
    Call<BaseResponse<List<TrafficReportResponse>>> getTrafficReport(@Query("lat") double lat,
                                                                     @Query("lng") double lng);

    @GET("api/report/segment/report-detail")
    Call<BaseResponse<ReportResponse>> getDetailTrafficReport(@Query("id") String id);

    @GET("api/report/segment/reports")
    Call<BaseResponse<List<ReportResponse>>> getOldTrafficReport(@Query("time") long date,
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
    Call<BaseResponse<String>> uploadFile(@Part MultipartBody.Part file);
}
