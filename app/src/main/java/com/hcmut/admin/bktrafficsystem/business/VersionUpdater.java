package com.hcmut.admin.bktrafficsystem.business;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.hcmut.admin.bktrafficsystem.BuildConfig;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.StatusResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.AppVersionResponse;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionUpdater {
    public static final String APP_VERSION_CLIENT_ID = "1";

    public static void checkNewVersion(final Activity activity) {
        RetrofitClient.getApiService().getCurrentAppVersionInfo(APP_VERSION_CLIENT_ID)
                .enqueue(new Callback<BaseResponse<AppVersionResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<AppVersionResponse>> call, Response<BaseResponse<AppVersionResponse>> response) {
                        int appVersionCode = BuildConfig.VERSION_CODE;
                        int remoteVersionCode = 2;
                        if (checkVersionCode(appVersionCode, remoteVersionCode)) {
                            MapActivity.androidExt.showDialog(
                                    activity,
                                    "Cập nhật phần mềm",
                                    "Phiên bản mới đã có sẵn, bạn có muốn cập nhật?",
                                    new ClickDialogListener.Yes() {
                                        @Override
                                        public void onCLickYes() {
                                            directToAppInCHPlay(activity);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<AppVersionResponse>> call, Throwable t) {

                    }
                });
    }

    private static boolean checkVersionCode(int appVersionCode, int remoteVersionCode) {
        return appVersionCode < remoteVersionCode;
    }

    private static void directToAppInCHPlay(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
