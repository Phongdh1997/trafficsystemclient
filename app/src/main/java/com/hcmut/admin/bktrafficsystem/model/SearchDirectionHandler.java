package com.hcmut.admin.bktrafficsystem.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.Coord;
import com.hcmut.admin.bktrafficsystem.model.response.DirectRespose;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDirectionHandler {
    public static final String TYPE_TIME = "time";
    public static final String TYPE_DISTANCE = "distance";

    public static void direct(Context context, String beginAddressString, String endAddressString, boolean isTimeType, DirectResultCallback listener) {
        Address beginAddress = MapUtil.getLatLngByAddressOrPlaceName(context, beginAddressString);
        Address endAddress = MapUtil.getLatLngByAddressOrPlaceName(context, endAddressString);
        if (beginAddress != null && endAddress != null) {
            LatLng beginLatLng = new LatLng(beginAddress.getLatitude(), beginAddress.getLongitude());
            LatLng endLatLng = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
            direct(context, beginLatLng, endLatLng, (isTimeType) ? TYPE_TIME : TYPE_DISTANCE, listener);
        }
    }

    private static void direct(Context context, LatLng startPoint, LatLng endPoint, String type, final DirectResultCallback listener) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Đang tìm đường..!", true);

        CallApi.createService()
                .getFindDirect(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, type)
                .enqueue(new Callback<BaseResponse<List<DirectRespose>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DirectRespose>>> call, Response<BaseResponse<List<DirectRespose>>> response) {
                        progressDialog.dismiss();
                        try {
                            List<Coord> directs = response.body().getData().get(0).getCoords();
                            if (directs.size() > 1) {
                                listener.onSuccess(directs);
//                                applyPolyLine(directs);
//                                pathId = response.body().getData().get(0).getPathId();
//                                toggleNotify(pathId, "true");
                                return;
                            }
                        } catch (Exception e) {}
                        listener.onFail();
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
        void onFail();
    }
}
