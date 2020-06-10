package com.hcmut.admin.bktrafficsystem.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallApi {
    private CallApi() {}

    private static Retrofit builder() {
        String base_URL = "https://api.bktraffic.com/";
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.readTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.connectTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.callTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.addInterceptor(logging);

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(base_URL)
                .client(okHttpClient.build())
                .build();
    }
    private static ApiService apiService;

    public static ApiService createService() {
        if (apiService == null) {
            apiService = builder().create(ApiService.class);
        }
        return apiService;
    }

}
