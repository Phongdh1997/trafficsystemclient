package com.hcmut.admin.bktrafficsystem.ui.map;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.statusrender.MoveToLoad;

import org.jetbrains.annotations.NotNull;

public class ProbeMapUi {

    /**
     * external view
     */
    private GoogleMap gmaps;
    private MoveToLoad moveToLoad;

    public ProbeMapUi(Context context,  @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        moveToLoad = new MoveToLoad(gmaps, context, mapFragment);
    }

    public void setupRenderStatus () {
        gmaps.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //moveToLoad.cameraMove(gmaps);
            }
        });

        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //moveToLoad.cameraMove(gmaps);
            }
        });
    }
}
