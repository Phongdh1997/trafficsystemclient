package com.hcmut.admin.bktrafficsystem.model.param;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.modules.data.model.UserLocation;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.report.ReportSendingFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRequest {
    public static final int MAX_VELOCITY = 80;
    public static final String [] reasons = {
            "Tắc đường", "Ngập lụt", "Có vật cản", "Tai nạn", "Công an", "Đường cấm"
    };

    private int velocity;
    private Double currentLat = null;
    private Double currentLng = null;
    private Double nextLat = null;
    private Double nextLng = null;
    private List<String> causes;
    private String description;
    private List<String> images;
    private String type = "user";
    private String address;

    public ReportRequest() {

    }

    /**
     * Contructor for post GPS data
     * @param prevLocation
     * @param currentLocation
     */
    public ReportRequest(@NotNull UserLocation prevLocation, @NotNull UserLocation currentLocation) {
        this.currentLat = prevLocation.getLatitude();
        this.currentLng = prevLocation.getLongitude();
        this.nextLat = currentLocation.getLatitude();
        this.nextLng = currentLocation.getLongitude();
        this.velocity = Math.round(UserLocation.calculateSpeed(prevLocation, currentLocation));
        this.type = "system";
    }

    public ReportRequest(int velocity, double currentLat, double currentLng, double nextLat, double nextLng, List<String> causes, String description, List<String> images) {
        this.velocity = velocity;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.nextLat = nextLat;
        this.nextLng = nextLng;
        this.causes = causes;
        this.description = description;
        this.images = images;
    }

    public boolean checkValidData(Context context) {
        if (currentLat == null || currentLng == null || nextLat == null || nextLng == null) {
            Toast.makeText(context,
                    "Vị trí cảnh báo chưa được chọn, vui lòng thử lại",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (velocity < 0 || velocity > MAX_VELOCITY) {
            Toast.makeText(context,
                    "Vận tốc phải lớn hơn 0 và nhỏ hơn " + MAX_VELOCITY,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void sendReport(final ReportSendingFragment fragment) {
        final Activity activity = fragment.getActivity();
        if (activity == null) return;

        CallApi.createService().postTrafficReport(MapActivity.currentUser.getAccessToken(), this)
                .enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                        Log.d("tessss: ", response.toString());
                        if (response.code() == 200 && response.body() != null && response.body().getCode() == 200) {
                            Log.e("ggg", response.body().getMessage());
                            MapActivity.androidExt.showSuccess(activity, "Gửi cảnh báo thành công");
                            fragment.clearReport();
                        } else {
                            MapActivity.androidExt.showErrorDialog(activity, "Gửi cảnh báo thất bại, vui lòng thử lại");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        MapActivity.androidExt.showErrorDialog(activity, "Gửi cảnh báo thất bại, vui lòng thử lại");
                    }
                });
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        if (velocity > 0) {
            this.velocity = velocity;
        } else {
            this.velocity = 1;
        }
    }

    public void setCurrentLatLng(LatLng latLng) {
        if (latLng != null) {
            currentLat = latLng.latitude;
            currentLng = latLng.longitude;
        }
    }

    public void setNextLatLng(LatLng latLng) {
        if (latLng != null) {
            nextLat = latLng.latitude;
            nextLng = latLng.longitude;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getNextLat() {
        return nextLat;
    }

    public void setNextLat(double nextLat) {
        this.nextLat = nextLat;
    }

    public double getNextLng() {
        return nextLng;
    }

    public void setNextLng(double nextLng) {
        this.nextLng = nextLng;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        if (causes != null && causes.size() > 0) {
            this.causes = causes;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 0) {
            this.description = description;
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        if (images != null && images.size() > 0) {
            this.images = images;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
