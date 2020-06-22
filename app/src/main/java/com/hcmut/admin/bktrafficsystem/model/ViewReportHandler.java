package com.hcmut.admin.bktrafficsystem.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmut.admin.bktrafficsystem.R;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.StatusRepositoryService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.StatusRemoteRepository;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportHandler {

    private Context context;
    private StatusRepositoryService statusRepository;
    private BitmapDescriptor icon;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ViewReportHandler(Context context) {
        this.context = context;
        statusRepository = new StatusRemoteRepository();
    }

    public void getUserReport(double lat, double lng, @NotNull final SegmentResultCallback callback) {
        RetrofitClient.getApiServerRetrofitService()
                .getTrafficStatus(lat, lng, 500)
                .enqueue(new Callback<StatusResponse<List<StatusRenderData>>>() {
                    @Override
                    public void onResponse(Call<StatusResponse<List<StatusRenderData>>> call, Response<StatusResponse<List<StatusRenderData>>> response) {
                        if (response.body() != null) {
                            if (icon == null) {
                                icon = bitmapDescriptorFromVector(context, R.drawable.ic_position_rating);
                            }
//                            final List<MarkerOptions> markerOptionsList = StatusRenderData
//                                    .parseMarkerOptionsList(response.body().getData(), icon);
                            final List<MarkerOptions> markerOptionsList = StatusRenderData
                                    .parseMarkerOptionsList(ReportRatingTest.getSegmentReport(), icon);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (markerOptionsList != null) {
                                        callback.onSuccess(markerOptionsList);
                                    } else {
                                        callback.onHaveNotResult();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse<List<StatusRenderData>>> call, Throwable t) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onHaveNotResult();
                            }
                        });
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int bgDrawableId) {
        Drawable background = ContextCompat.getDrawable(context, bgDrawableId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public interface SegmentResultCallback {
        void onSuccess(List<MarkerOptions> markerOptionsList);
        void onHaveNotResult();
    }
}
