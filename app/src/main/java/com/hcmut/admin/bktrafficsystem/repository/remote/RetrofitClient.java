package com.hcmut.admin.bktrafficsystem.repository.remote;

import android.os.Process;

import com.hcmut.admin.bktrafficsystem.business.PriorityThreadFactory;
import com.hcmut.admin.bktrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.bktrafficsystem.repository.remote.API.API_VOHService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    private static API_VOHService APIVOHService;
    private static APIService apiService;

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        THREAD_POOL_EXECUTOR.setThreadFactory(new PriorityThreadFactory(
                Process.THREAD_PRIORITY_BACKGROUND, "Threadpool"));
    }

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

    public static API_VOHService getAPIVOHService() {
        if (APIVOHService == null) {
            String base_URL = "https://bktraffic.com:3000";
            APIVOHService = builder(base_URL).create(API_VOHService.class);
        }
        return APIVOHService;
    }

    public static APIService getApiService() {
        if (apiService == null) {
//            String baseURL = "http://172.17.24.64:3000";
            String baseURL = "https://api.bktraffic.com";
            apiService = builder(baseURL).create(APIService.class);
        }
        return apiService;
    }
}
