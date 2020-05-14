package com.hcmut.admin.bktrafficsystem.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.StatusOverlayRender;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.repository.remote.retrofit.model.response.StatusRenderData;

public class CustomTileProvider implements TileProvider {
    private static final String TAG = "PointTileOverlay";
    private final int mTileSize = 256;
    private final SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
    private final int mScale = 1;
    private final int mDimension = mScale * mTileSize;
    private Paint paint;

    private List<StatusRenderData> statusDataSource;

    private static final int DEFAULT_COLOR = Color.BLACK;

    public CustomTileProvider(List<StatusRenderData> statusDataSource) {
        this.statusDataSource = statusDataSource;
    }

    /**
     * Update data source for new polyline render
     */
    public synchronized void addDataSource(List<StatusRenderData> statusDataSource) {
        if (this.statusDataSource == null) {
            this.statusDataSource = new ArrayList<>();
        }
        if (statusDataSource != null) {
            this.statusDataSource.addAll(statusDataSource);
        }
    }

    public synchronized void clearDataSource () {
        this.statusDataSource.clear();
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        Log.d(TAG, "zoom int: " + zoom);
        Matrix matrix = new Matrix();

        /*The scale factor in the transformation matrix is 1/10 here because I scale up the tiles for drawing.
         * Why? Well, the spherical mercator projection doesn't seem to quite provide the resolution I need for
         * scaling up at high zoom levels. This bypasses it without needing a higher tile resolution.
         */
        float scale = ((float) Math.pow(2, zoom) * mScale / 10);
        matrix.postScale(scale, scale);
        matrix.postTranslate(-x * mDimension, -y * mDimension);

        Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888); //save memory on old phones
        Canvas c = new Canvas(bitmap);
        c.setMatrix(matrix);
        c = drawCanvasFromArray(c, zoom);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return new Tile(mDimension, mDimension, baos.toByteArray());
    }

    /**
     * Here the Canvas can be drawn on based on data provided from a Spherical Mercator Projection
     *
     * @param c
     * @param zoom
     * @return
     */
    private Canvas drawCanvasFromArray(Canvas c, int zoom) {

        paint = new Paint();

        //Line features
        paint.setStrokeWidth(getLineWidth(zoom));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeCap(Cap.ROUND);
        paint.setStrokeJoin(Join.ROUND);
        paint.setShadowLayer(0, 0, 0, 0);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(getAlpha(zoom));

        if (statusDataSource != null) {
            List<LatLng> route;
            float startX;
            float startY;
            float stopX;
            float stopY;
            Log.e("tile data source", "size " + statusDataSource.size());
            for (StatusRenderData statusRenderDataItem : statusDataSource) {
                 route = statusRenderDataItem.getLatLngPolyline();
                if (route != null && route.size() > 1) {
                    // start point
                    Point screenPt1 = mProjection.toPoint(route.get(0));
                    startX = (float) screenPt1.x * 10;
                    startY = (float) screenPt1.y * 10;

                    // stop point
                    Point screenPt2 = mProjection.toPoint(route.get(1));
                    stopX = (float) screenPt2.x * 10;
                    stopY = (float) screenPt2.y * 10;

                    // draw polyline
                    paint.setColor(Color.parseColor(statusRenderDataItem.getColor()));
                    c.drawLine(startX, startY, stopX, stopY, paint);
                }
            }
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
        switch (zoom) {
            case 21:
            case 20:
                return 0.0001f;
            case 19:
                return 0.00025f;
            case 18:
                return 0.0005f;
            case 17:
                return 0.0005f;
            case 16:
                return 0.001f;
            case 15:
                return 0.001f;
            case 14:
                return 0.001f;
            case 13:
                return 0.002f;
            case 12:
                return 0.003f;
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