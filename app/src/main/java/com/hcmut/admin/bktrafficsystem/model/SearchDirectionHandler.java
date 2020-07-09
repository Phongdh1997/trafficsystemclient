package com.hcmut.admin.bktrafficsystem.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.Coord;
import com.hcmut.admin.bktrafficsystem.model.response.DirectRespose;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDirectionHandler {
    public static final String TYPE_TIME = "time";
    public static final String TYPE_DISTANCE = "distance";

    public static void direct(final Context context,
                              final LatLng startPoint,
                              final LatLng endPoint,
                              final boolean isTimeType,
                              final DirectResultCallback listener) {
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (startPoint != null && endPoint != null) {
                    GeoApiContext geoApiContext = new GeoApiContext()
                            .setApiKey("AIzaSyBfloTm067WfYy3ZiE2BiubYjOhv4H-Jrw");
                    final LatLng optimizedStartPoint = MapUtil.snapToRoad(geoApiContext, startPoint);
                    final LatLng optimizedEndPoint = MapUtil.snapToRoad(geoApiContext, endPoint);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            direct(context,
                                    optimizedStartPoint == null ? startPoint : optimizedStartPoint,
                                    optimizedEndPoint == null ? endPoint : optimizedEndPoint,
                                    (isTimeType) ? TYPE_TIME : TYPE_DISTANCE, listener);
                        }
                    });
                }
            }
        });
    }

    public static LatLng addressStringToLatLng(Context context, String beginAddressString) {
        Address address = MapUtil.getLatLngByAddressOrPlaceName(context, beginAddressString);
        if (address != null) {
            return new LatLng(address.getLatitude(), address.getLongitude());
        }
        return null;
    }

    private static void direct(Context context, LatLng startPoint, LatLng endPoint, String type, final DirectResultCallback listener) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Đang tìm đường..!", true);

        CallApi.createService()
                .getFindDirect(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, type)
                .enqueue(new Callback<BaseResponse<List<DirectRespose>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DirectRespose>>> call, Response<BaseResponse<List<DirectRespose>>> response) {
                        Log.e("fad", response.toString());
                        progressDialog.dismiss();
                        try {
                            List<Coord> directs = response.body().getData().get(0).getCoords();
                            if (directs.size() > 1) {
                                listener.onSuccess(directs);
                                return;
                            }
                        } catch (Exception e) {}
                        listener.onHaveNoData();
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<DirectRespose>>> call, Throwable t) {
                        progressDialog.dismiss();
                        listener.onFail();
                    }
                });
    }

    public interface DirectResultCallback {
        void onSuccess(List<Coord> directs);
        void onHaveNoData();
        void onFail();
    }
}
