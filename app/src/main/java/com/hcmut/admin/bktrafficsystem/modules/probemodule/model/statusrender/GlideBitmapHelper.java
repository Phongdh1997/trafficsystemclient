package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.request.FutureTarget;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide.BitmapGlideModel;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide.GlideApp;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import java.util.concurrent.Executor;

public class GlideBitmapHelper {
    private Context context;

    private static GlideBitmapHelper glideBitmapHelper;

    private GlideBitmapHelper(Context context) {
        this.context = context;
    }

    public static GlideBitmapHelper getInstance (Context context) {
        if (glideBitmapHelper == null) {
            glideBitmapHelper = new GlideBitmapHelper(context);
        }
        return glideBitmapHelper;
    }

    public void setMemoryCategory(MemoryCategory category) {
        GlideApp.get(context).setMemoryCategory(category);
    }

    public void clearMemory() {
        GlideApp.get(context).clearMemory();
    }

    public void clearDiskCache (Executor executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                GlideApp.get(context).clearDiskCache();
            }
        });
    }

    /**
     * TODO:
     * @return
     */
    public Bitmap loadBitmapFromGlide(TileCoordinates tile) {
        BitmapGlideModel model = new BitmapGlideModel(tile, null);
        FutureTarget<Bitmap> target = GlideApp
                .with(context)
                .asBitmap()
                .load(model)
                .submit();
        try {
            return target.get();
        } catch (Exception e) {}
        return null;
    }

    @SuppressLint("CheckResult")
    public void storeBitmapToGlide (TileCoordinates tile, Bitmap bitmap) {
        BitmapGlideModel model = new BitmapGlideModel(tile, bitmap);
        GlideApp.with(context).load(model).submit();
    }
}
