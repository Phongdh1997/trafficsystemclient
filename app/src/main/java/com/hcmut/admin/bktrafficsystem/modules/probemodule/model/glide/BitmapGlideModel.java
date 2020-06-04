package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.tile.TileCoordinates;

import org.jetbrains.annotations.NotNull;

public class BitmapGlideModel {
    private TileCoordinates key;
    private Bitmap bitmap;

    public BitmapGlideModel(@NotNull TileCoordinates key, @Nullable Bitmap bitmap) {
        this.key = key;
        this.bitmap = bitmap;
    }

    public TileCoordinates getKey() {
        return key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BitmapGlideModel) {
            BitmapGlideModel modle = (BitmapGlideModel) obj;
            return key.equals(modle.getKey());
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return key.toString();
    }
}
