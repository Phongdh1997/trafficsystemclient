package com.hcmut.admin.googlemapapitest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;

import static com.hcmut.admin.googlemapapitest.util.ImageUtils.getRealPathFromURI;
import static com.hcmut.admin.googlemapapitest.util.ImageUtils.getStreamBitMapFromImage;

public class Async_ResizeImageFromUri extends AsyncTask<Integer, Void, String> {
    private final Uri uri;
    private Context context;
    private AsyncResponse delegate;

    // Constructor
    public Async_ResizeImageFromUri(Context context, Uri uri, AsyncResponse delegate) {
        this.uri = uri;
        this.context = context;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    // Compress and Decode image in background.
    @Override
    protected String doInBackground(Integer... params) {
        Bitmap bitmap = getStreamBitMapFromImage(new File(getRealPathFromURI(context, uri)));
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 375, 375 * bitmap.getHeight() / bitmap.getWidth(), true);
//        resized.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), resized, "Title", null);
    }

    // This method is run on the UI thread
    @Override
    protected void onPostExecute(String path) {
        delegate.processFinish(path);
    }
}