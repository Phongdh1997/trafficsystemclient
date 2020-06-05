package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

@GlideModule
public class TrafficGlideModule extends AppGlideModule {
    public static BitmapPool BITMAP_POOL;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 350; // 350 MB
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(1)
                .setBitmapPoolScreens(1)
                .build();
        BITMAP_POOL = new LruBitmapPool(calculator.getBitmapPoolSize());
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        builder.setBitmapPool(BITMAP_POOL);
        builder.setDiskCache(
                new ExternalPreferredCacheDiskCacheFactory(
                        context,
                        "cacheFolderName",
                        diskCacheSizeBytes));
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .disallowHardwareConfig());
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(BitmapGlideModel.class, Bitmap.class, new TrafficModelLoader.TrafficModelLoaderFactory());
        registry.append(Bitmap.class, new TrafficResourceEncoder());
        registry.append(File.class, Bitmap.class, new TrafficResourceDecoder());
    }
}
