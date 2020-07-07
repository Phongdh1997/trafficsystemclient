package com.hcmut.admin.bktrafficsystem.model.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.GoogleSignInData;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.UserResponse;
import com.hcmut.admin.bktrafficsystem.ui.LoginActivity;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User {
    private String accountType;
    private Boolean mLocationPermissionsGranted;
    private String userId;
    private String userName;
    private String phoneNumber;
    private float evaluation_score;
    private String imgUrl;
    private String userEmail;
    private String accessToken;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean getmLocationPermissionsGranted() {
        return mLocationPermissionsGranted;
    }

    public void setmLocationPermissionsGranted(Boolean mLocationPermissionsGranted) {
        this.mLocationPermissionsGranted = mLocationPermissionsGranted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getEvaluation_score() {
        return evaluation_score;
    }

    public void setEvaluation_score(int evaluation_score) {
        this.evaluation_score = evaluation_score;
    }

    public void updateUser(final Activity activity, String name, String phone) {
        if (activity == null) return;

        final ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Đang lưu thông tin...", true);
        CallApi.createService().updateUserInfo(accessToken, name, null, null, phone).enqueue(new Callback<BaseResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserResponse>> call, Response<BaseResponse<UserResponse>> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().getCode() == 200) {
                    UserResponse userResponse = response.body().getData();
                    phoneNumber = userResponse.getPhoneNumber();
                    userName = userResponse.getName();
                    SharedPrefUtils.saveUser(activity.getApplicationContext(), User.this);
                    MapActivity.androidExt.showSuccess(activity, "Lưu thành công");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<UserResponse>> call, Throwable t) {
                progressDialog.dismiss();
                MapActivity.androidExt.showErrorDialog(activity, "Có lỗi, cập nhật thất bại");
            }
        });
    }

    public void logout(final MapActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Đăng xuất")
                .setMessage("Bạn chắc chắn muốn đăng xuất?")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (accountType) {
                            case "facebook": {
                                LoginManager.getInstance().logOut();
                                break;
                            }
                            case "google": {
                                GoogleSignInClient mGoogleSignInClient = GoogleSignInData.getValue();
                                if (mGoogleSignInClient != null) {
                                    mGoogleSignInClient.signOut();
                                }
                                break;
                            }
                        }
                        SharedPrefUtils.saveUser(activity, null);
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }
}
