package com.hcmut.admin.bktrafficsystem.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.UserResponse;
import com.hcmut.admin.bktrafficsystem.model.user.User;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    private final int RESULT_LOAD_IMG = 101;

    private EditText name, phoneNumber, email;

    private AppCompatImageView ivBack;

    private AppCompatButton btnUpdate;

    private CircleImageView avatar;

    private AppCompatRatingBar ratingBar;

    private User user;

    private ProgressDialog progressDialog;

    private boolean isAvatarChange = false;

    private String filePath = null;

    private String avatarString = null;

    AndroidExt androidExt = new AndroidExt();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        init();
        onEvent();
        onTextChange();
    }

    private void onEvent() {
        ivBack.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        avatar.setOnClickListener(this);
        findViewById(R.id.ctlInformation).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    name.clearFocus();
                    phoneNumber.clearFocus();
                    email.clearFocus();
                }
                return false;
            }
        });
    }

    private void init() {
        name = findViewById(R.id.edt_name);
        phoneNumber = findViewById(R.id.edt_phone_number);
        email = findViewById(R.id.edt_email);
        ratingBar = findViewById(R.id.ratingBar);
        btnUpdate = findViewById(R.id.btn_update);
        avatar = findViewById(R.id.avt_imv);
        ivBack = findViewById(R.id.ivBack);
        findViewById(R.id.ic_photo_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
        user = SharedPrefUtils.getUser(this);
        String imgUrl = user.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) Glide.with(InformationActivity.this).load(imgUrl).into(avatar);
        else Glide.with(this).load(R.drawable.ic_user_avatar).into(avatar);
        name.setText((user.getUserName() != null) ? user.getUserName() : "");
        email.setText((user.getUserEmail() != null) ? user.getUserEmail() : "");
        phoneNumber.setText((user.getPhoneNumber() != null && ((!user.getPhoneNumber().equals("")))) ? user.getPhoneNumber() : "");


        ratingBar.setRating((user.getEvaluation_score() * 10) / 2);
        if (!user.getAccountType().isEmpty()) {
            email.setEnabled(false);
            email.setTextColor(getColor(R.color.iron));
        }
    }

    private void onTextChange() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnUpdate.setEnabled(isAvatarChange || !(name.getText().toString().equals(user.getUserName())
                        && phoneNumber.getText().toString().equals(user.getPhoneNumber())
                        && email.getText().toString().equals(user.getUserEmail())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnUpdate.setEnabled(isAvatarChange || !(name.getText().toString().equals(user.getUserName())
                        && phoneNumber.getText().toString().equals(user.getPhoneNumber())
                        && email.getText().toString().equals(user.getUserEmail())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnUpdate.setEnabled(isAvatarChange || !(name.getText().toString().equals(user.getUserName())
                        && phoneNumber.getText().toString().equals(user.getPhoneNumber())
                        && email.getText().toString().equals(user.getUserEmail())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btn_update:
                progressDialog = ProgressDialog.show(InformationActivity.this, "", "Đang tải..", true);
                onResponse();
                btnUpdate.setEnabled(false);
                isAvatarChange = false;
                name.clearFocus();
                phoneNumber.clearFocus();
                email.clearFocus();
                break;
            case R.id.avt_imv:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            filePath = getRealPathFromUri(selectedImage);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatar.setImageBitmap(bitmap);
            btnUpdate.setEnabled(true);
            isAvatarChange = true;

            uploadAvatar();

        } else {
            androidExt.showErrorDialog(InformationActivity.this, "Bạn chưa chọn ảnh");
        }
    }

    private void uploadAvatar() {
        if (filePath != null) {
            File file = new File(filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part imageBody = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

            CallApi.createService().uploadFile(imageBody)
                    .enqueue(new Callback<BaseResponse<String>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                            if (response.body() != null) {
                                avatarString = response.body().getData();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {

                        }
                    });
        }
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void onResponse(){
        CallApi.createService().updateUserInfo(user.getAccessToken(), name.getText().toString(), email.getText().toString(),
                avatarString, phoneNumber.getText().toString()).enqueue(new Callback<BaseResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserResponse>> call, Response<BaseResponse<UserResponse>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    UserResponse userResponse = response.body().getData();
                    user.setPhoneNumber(userResponse.getPhoneNumber());
                    user.setImgUrl(userResponse.getAvatar());
                    user.setUserName(userResponse.getName());
                    user.setUserEmail(userResponse.getEmail());
                    SharedPrefUtils.saveUser(InformationActivity.this, user);
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);

                    androidExt.showSuccessDialog(InformationActivity.this, "Cập nhật thành công", new ClickDialogListener.OK() {
                        @Override
                        public void onCLickOK() {
                        }
                    });
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse<UserResponse>> call, Throwable t) {
                progressDialog.dismiss();
                androidExt.showErrorDialog(InformationActivity.this, "Có lỗi, cập nhật thất bại");
            }
        });
    }
}
