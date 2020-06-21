package com.hcmut.admin.bktrafficsystem.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallApi {
    private CallApi() {}

    private static Retrofit builder(String url) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.readTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.connectTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.callTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.addInterceptor(logging);

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(okHttpClient.build())
                .build();
    }
    private static ApiService apiService;
    private static BktrafficService bktrafficService;

    public static ApiService createService() {
        if (apiService == null) {
            String base_URL = "https://api.bktraffic.com/";
            apiService = builder(base_URL).create(ApiService.class);
        }
        return apiService;
    }

    public static BktrafficService getBkTrafficService() {
        if (bktrafficService == null) {
            String base_URL = "https://bktraffic.com:3000";
            bktrafficService = builder(base_URL).create(BktrafficService.class);
        }
        return bktrafficService;
    }

}
