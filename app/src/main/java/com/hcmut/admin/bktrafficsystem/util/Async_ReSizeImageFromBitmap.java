package com.hcmut.admin.bktrafficsystem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;

public class Async_ReSizeImageFromBitmap extends AsyncTask<Integer, Void, String> {
    private final Bitmap bitmap;
    private Context context;
    private AsyncResponse delegate;

    // Constructor
    public Async_ReSizeImageFromBitmap(Context context, Bitmap bitmap, AsyncResponse delegate) {
        this.bitmap = bitmap;
        this.context = context;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    // Compress and Decode image in background.
    @Override
    protected String doInBackground(Integer... params) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 375, 375*bitmap.getHeight()/bitmap.getWidth(), true);
//        resized.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), resized, "Title", null);
    }

    // This method is run on the UI thread
    @Override
    protected void onPostExecute(String path) {
        delegate.processFinish(path);
    }
}