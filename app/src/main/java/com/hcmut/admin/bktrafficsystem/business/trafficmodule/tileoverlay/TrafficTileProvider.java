package com.hcmut.admin.bktrafficsystem.business.trafficmodule.tileoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.bktrafficsystem.business.TileCoordinates;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.DataLoadingState;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.TrafficDataLoader;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.TrafficBitmap;
import com.hcmut.admin.bktrafficsystem.business.trafficmodule.groundoverlay.BitmapLineData;
import com.hcmut.admin.bktrafficsystem.repository.exportCSV.OutputCSVFile;
import com.hcmut.admin.bktrafficsystem.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.StatusRenderData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrafficTileProvider implements TileProvider {
    public static final int MAX_ZOOM_RENDER = 16;
    private int count = 50;
    private List<long[]> tests = new ArrayList<>();

    private TrafficBitmap trafficBitmap;
    private TrafficDataLoader trafficDataLoader;

    public TrafficTileProvider(Context context) {
        trafficBitmap = new TrafficBitmap();
        trafficDataLoader = TrafficDataLoader.getInstance(context);
    }

    public void setDataLoadingState(DataLoadingState dataLoadingState) {
        trafficDataLoader.setDataLoadingState(dataLoadingState);
    }

    @Override
    public Tile getTile(int x, int y, int z) {
        if (z < 15 || z > MAX_ZOOM_RENDER) return NO_TILE;
        try {
            TileCoordinates renderTile = TileCoordinates.getTileCoordinates(x, y, z);
            //trafficDataLoader.loadTrafficDataFromServerAsync(renderTile, true);
            return generateTileFromRemoteDate(renderTile);
        } catch (Exception e) {
        }
        return null;
    }

    private Tile generateTileFromRemoteDate(TileCoordinates renderTile) {
        long loadStart = System.currentTimeMillis();
        List<StatusRenderData> statusDatas = trafficDataLoader.loadTrafficDataFromServer(renderTile);
        long loadEnd = System.currentTimeMillis();

        long renderStart = System.currentTimeMillis();
        int scale = getScaleByZoom(renderTile);
        List<BitmapLineData> bitmapLineDataList = StatusRenderData.parseBitmapLineData(statusDatas);
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, bitmapLineDataList, scale, null);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();

            long renderEnd = System.currentTimeMillis();


            if (count > 0) {
                tests.add(new long[] {(loadEnd - loadStart), (renderEnd - renderStart), bitmapLineDataList.size()});
                Log.e("time time", count + ". Load data from server: " + (loadEnd - loadStart) + "; Render status: " + (renderEnd - renderStart));
            }
            if (count == 0) {
                List<String[]> inputs = new ArrayList<>();
                long[] sum = new long[] {0, 0, 0};
                for (long[] d : tests) {
                    inputs.add(new String[] {"" + d[0], "" + d[1], "" + d[2]});
                    sum[0] += d[0];
                    sum[1] += d[1];
                    sum[2] += d[2];
                }
                sum[0] = sum[0] / inputs.size();
                sum[1] = sum[1] / inputs.size();
                sum[2] = sum[2] / inputs.size();
                inputs.add(new String[]{"Total Load time", "total render", "total segment"});
                inputs.add(new String[] {"" + sum[0], "" + sum[1], "" + sum[2]});
                OutputCSVFile.writeLocationList(inputs, OutputCSVFile.RENDER_STATUS_SPEED_TEST_FILE_NAME);
            }
            count--;
            return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
        }
        return null;
    }

    private Tile generateTile(TileCoordinates renderTile) {
        int scale = getScaleByZoom(renderTile);
        List<StatusRenderDataEntity> dataList = trafficDataLoader.loadDataFromLocal(renderTile);
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, dataList, scale, null);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
        }
        return null;
    }

    private int getScaleByZoom(TileCoordinates tile) {
        switch (tile.z) {
            case 15:
                return 2;
            default:
                return 2;
        }
    }
}
