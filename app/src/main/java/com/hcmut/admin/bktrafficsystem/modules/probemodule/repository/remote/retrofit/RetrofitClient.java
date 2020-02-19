package com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.APIServerRetrofitService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.API.ProbeServerRetrofitService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BkTrafficAPI_BASE_URL = "https://api.bktraffic.com";
    private static final String ProbeServerBASE_URL = "http://45.77.254.77:2222";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(25, TimeUnit.SECONDS)
            .connectTimeout(25, TimeUnit.SECONDS)
            .build();
    private static Retrofit bktrafficAPI_RetrofitClient = new Retrofit.Builder()
            .baseUrl(BkTrafficAPI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();

    private static Retrofit probeServerRetrofitClient = new Retrofit.Builder()
            .baseUrl(ProbeServerBASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static APIServerRetrofitService getApiServerRetrofitService() {
        return bktrafficAPI_RetrofitClient.create(APIServerRetrofitService.class);
    }

    public static ProbeServerRetrofitService getProbeServerRetrofitService() {
        return probeServerRetrofitClient.create(ProbeServerRetrofitService.class);
    }
}
