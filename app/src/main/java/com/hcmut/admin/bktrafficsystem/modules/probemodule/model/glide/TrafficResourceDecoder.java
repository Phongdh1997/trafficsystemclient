package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.TrafficBitmap;

import java.io.File;
import java.io.IOException;

public class TrafficResourceDecoder implements ResourceDecoder<File, Bitmap> {
    @Override
    public boolean handles(@NonNull File source, @NonNull Options options) throws IOException {
        return true;
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(@NonNull File source, int width, int height, @NonNull Options options) throws IOException {
        int size = TrafficBitmap.mTileSize * TrafficBitmap.TILE_ZOOM_15_SCALE;
        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(source.getPath(), bitmapOption);
        Log.e("glide", "decode");
        return BitmapResource.obtain(bitmap, TrafficGlideModule.BITMAP_POOL);
    }
}
