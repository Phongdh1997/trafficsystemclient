package com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.local.room.entity.StatusRenderDataEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

public class TrafficBitmap {
    public static final int TILE_ZOOM_15_SCALE = 2;
    public static final int mTileSize = 256;
    private static final SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
    private static final int mScale = TILE_ZOOM_15_SCALE;
    public static final int mDimension = mScale * mTileSize;
    private static final int DEFAULT_COLOR = Color.BLACK;

    public TrafficBitmap() {
    }

    /**
     * - Draw bitmap
     * - call invalidate() to render tile
     * - cache bitmap to Glide
     * @param tile
     * @param lineDataList
     */
    public <T> Bitmap createTrafficBitmap (TileCoordinates tile, @Nullable List<T> lineDataList) {
        return drawBitmap(tile.x, tile.y, tile.z, lineDataList);
    }

    private <T> Bitmap drawBitmap(int x, int y, int zoom, @Nullable List<T> lineDataList) {
        if (lineDataList == null || lineDataList.size() == 0) {
            return null;
        }
        Log.e("Tile", "render status, size " + lineDataList.size());
        Matrix matrix = new Matrix();

        float scale = ((float) Math.pow(2, zoom) * mScale / 10);
        matrix.postScale(scale, scale);
        matrix.postTranslate(-x * mDimension, -y * mDimension);

        Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888); //save memory on old phones
        Canvas c = new Canvas(bitmap);
        c.setMatrix(matrix);
        c = drawCanvasFromArray(c, zoom, lineDataList);
        return bitmap;
    }

    /**
     * Here the Canvas can be drawn on based on data provided from a Spherical Mercator Projection
     *
     * @param c
     * @param zoom
     * @return
     */
    private <T> Canvas drawCanvasFromArray(Canvas c, int zoom, @NotNull List<T> lineDataList) {
        //Line features
        Paint paint = new Paint();
        paint.setStrokeWidth(getLineWidth(zoom));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setShadowLayer(0, 0, 0, 0);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(getAlpha(zoom));
        paint.setAntiAlias(true);

        T item = lineDataList.get(0);
        if (item instanceof BitmapLineData) {
            drawByBitmapLineData(c, paint, (List<BitmapLineData>) lineDataList);
        } else if (item instanceof StatusRenderDataEntity) {
            drawByRenderDataEntity(c, paint, (List<StatusRenderDataEntity>) lineDataList);
        }
        return c;
    }

    private void drawByBitmapLineData(Canvas c, Paint paint, List<BitmapLineData> lineDataList) {
        if (lineDataList != null) {
            float startX;
            float startY;
            float stopX;
            float stopY;
            for (BitmapLineData lineData : lineDataList) {
                // start point
                Point screenPt1 = mProjection.toPoint(lineData.startLatLng);
                startX = (float) screenPt1.x * 10;
                startY = (float) screenPt1.y * 10;

                // stop point
                Point screenPt2 = mProjection.toPoint(lineData.endLatLng);
                stopX = (float) screenPt2.x * 10;
                stopY = (float) screenPt2.y * 10;

                // draw polyline
                paint.setColor(Color.parseColor(lineData.color));
                c.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }

    private void drawByRenderDataEntity(Canvas c, Paint paint, List<StatusRenderDataEntity> lineDataList) {
        if (lineDataList != null) {
            float startX;
            float startY;
            float stopX;
            float stopY;
            for (StatusRenderDataEntity lineData : lineDataList) {
                // start point
                Point screenPt1 = mProjection.toPoint(lineData.getStartLatlng());
                startX = (float) screenPt1.x * 10;
                startY = (float) screenPt1.y * 10;

                // stop point
                Point screenPt2 = mProjection.toPoint(lineData.getEndLatlng());
                stopX = (float) screenPt2.x * 10;
                stopY = (float) screenPt2.y * 10;

                // draw polyline
                paint.setColor(Color.parseColor(lineData.color));
                c.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }

    /**
     * This will let you adjust the line width based on zoom level
     *
     * @param zoom
     * @return
     */
    private float getLineWidth(int zoom) {
        Log.e("zoom", "" + zoom);
        switch (zoom) {
            case 21:
            case 20:
            case 19:
                return 0.000123f;
            case 18:
                return 0.00015f;//ok
            case 17:
                return 0.00025f; //ok
            case 16:
                return 0.0003f; //ok
            case 15:
                return 0.0003f; //0k
            default:
                return 0f;
        }
    }

    /**
     * This will let you adjust the alpha value based on zoom level
     *
     * @param zoom
     * @return
     */
    private int getAlpha(int zoom) {

        switch (zoom) {
            case 20:
                return 140;
            case 19:
                return 140;
            case 18:
                return 140;
            case 17:
                return 140;
            case 16:
                return 180;
            case 15:
                return 180;
            case 14:
                return 180;
            default:
                return 255;
        }
    }
}
