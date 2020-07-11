package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;

import java.util.List;

import javax.annotation.Nullable;

@Deprecated
public class TileBitmap {
    private final int TILE_ZOOM_15_SCALE = 3;
    private final int mTileSize = 256;
    private final SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
    private final int mScale = TILE_ZOOM_15_SCALE;
    private final int mDimension = mScale * mTileSize;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private Paint paint = new Paint();

    public Bitmap getTileWithScale(int x, int y, int zoom, @Nullable List<StatusRenderDataEntity> statusDatas) {
        if (statusDatas == null || statusDatas.size() == 0) {
            return null;
        }
        Log.e("Tile", "render status, size " + statusDatas.size());
        Matrix matrix = new Matrix();

        float scale = ((float) Math.pow(2, zoom) * mScale / 10);
        matrix.postScale(scale, scale);
        matrix.postTranslate(-x * mDimension, -y * mDimension);

        Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888); //save memory on old phones
        Canvas c = new Canvas(bitmap);
        c.setMatrix(matrix);
        c = drawCanvasFromArray(c, zoom, statusDatas);
        return bitmap;
    }

    /**
     * Here the Canvas can be drawn on based on data provided from a Spherical Mercator Projection
     *
     * @param c
     * @param zoom
     * @return
     */
    private Canvas drawCanvasFromArray(Canvas c, int zoom, List<StatusRenderDataEntity> statusDatas) {
        //Line features
        paint.setStrokeWidth(getLineWidth(zoom));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setShadowLayer(0, 0, 0, 0);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(getAlpha(zoom));

        //Path path = new Path();

        if (statusDatas != null) {
            float startX;
            float startY;
            float stopX;
            float stopY;
            LatLng startPoint;
            LatLng endPoint;
            for (StatusRenderDataEntity statusRenderDataEntity : statusDatas) {
                startPoint = statusRenderDataEntity.getStartLatlng();
                endPoint = statusRenderDataEntity.getEndLatlng();
                if (startPoint != null && endPoint != null) {
                    // start point
                    Point screenPt1 = mProjection.toPoint(startPoint);
                    startX = (float) screenPt1.x * 10;
                    startY = (float) screenPt1.y * 10;

                    // stop point
                    Point screenPt2 = mProjection.toPoint(endPoint);
                    stopX = (float) screenPt2.x * 10;
                    stopY = (float) screenPt2.y * 10;

                    // draw polyline
                    paint.setColor(Color.parseColor(statusRenderDataEntity.color));
                    c.drawLine(startX, startY, stopX, stopY, paint);

                    //path.moveTo(startX, startY);
                    //path.lineTo(stopX, stopY);
                }
            }
            //c.drawPath(path, paint);
        }
        return c;
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
                return 0.00035f; //0k
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