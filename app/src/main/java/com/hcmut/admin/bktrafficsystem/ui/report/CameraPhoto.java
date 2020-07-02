package com.hcmut.admin.bktrafficsystem.ui.report;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.ImageDownloader;
import com.hcmut.admin.bktrafficsystem.util.Async_ReSizeImageFromBitmap;
import com.hcmut.admin.bktrafficsystem.util.Async_ResizeImageFromUri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.hcmut.admin.bktrafficsystem.util.ImageUtils.getGalleryIntents;
import static com.hcmut.admin.bktrafficsystem.util.ImageUtils.getRealPathFromURI;

public class CameraPhoto {
    public static final int IMAGE_PERMISSION_CODE = 111;
    public static final int IMAGE_REQUEST = 112;

    private static PhotoUploadCallback photoUploadCallback;

    public static void registerPhotoUploadCallback(PhotoUploadCallback listener) {
        photoUploadCallback = listener;
    }

    public static void unRegisterPhotoUploadCallback() {
        photoUploadCallback = null;
    }

    public static void checkPermission(Activity activity) {
        if (activity == null) return;
        Context context = activity.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);
            } else {
                getPhotoIntent(activity);
            }
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
        if (requestCode == IMAGE_PERMISSION_CODE) {
            Log.e("test", "length" + grantResults.length);
            if ((grantResults.length > 2) && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                getPhotoIntent(activity);
            }
        }
    }

    public static void onActivityResult(Context context, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            handleImageResult(context, data);
        }
    }

    private static void handleImageResult(Context context, Intent data) {
        Bitmap bitmap = null;
        try {
            if (data.getData() == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {}
        uploadFile(bitmap);
    }

    private static void uploadFile(final Bitmap bitmap) {
        if (photoUploadCallback != null) {
            if (bitmap == null) {
                photoUploadCallback.onUpLoadFail();
                return;
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            RequestBody requestBody = RequestBody.create(byteArray, MediaType.parse("image/*"));
            CallApi.createService().uploadFile(requestBody)
                    .enqueue(new Callback<BaseResponse<String>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<String>> call, final Response<BaseResponse<String>> response) {
                            // TODO: neu thanh cong thi set image vao imageview
                            if (response.body() != null &&
                                    response.body().getData() != null &&
                                    response.code() == 200) {
                                photoUploadCallback.onUpLoaded(bitmap, response.body().getData());
                            } else {
                                photoUploadCallback.onUpLoadFail();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                            photoUploadCallback.onUpLoadFail();
                        }
                    });
        }
    }

    private static void getPhotoIntent(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) return;

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        // collect all camera intents if Camera permission is available
        allIntents.add(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

        List<Intent> galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK);
        }
        allIntents.addAll(galleryIntents);

        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, context.getString(R.string.title_take_photo));

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        activity.startActivityForResult(chooserIntent, IMAGE_REQUEST);
    }

    public interface PhotoUploadCallback {
        void onUpLoaded(Bitmap bitmap, String url);
        void onUpLoadFail();
    }
}
