package com.hcmut.admin.bktrafficsystem.modules.probemodule.event;

import com.google.android.gms.maps.model.LatLng;

public class StatusRenderEvent {
    private boolean isRendering = true;
    private boolean isClearRender = true;
    private LatLng cameraTarget;
    private float zoom = 0.0f;

    public StatusRenderEvent() {
    }

    public StatusRenderEvent(boolean isClearRender) {
        this.isClearRender = isClearRender;
    }

    public boolean isClearRender() {
        return isClearRender;
    }

    public boolean isHaveCameraTarget() {
        return cameraTarget != null;
    }

    public LatLng getCameraTarget() {
        return cameraTarget;
    }

    public void setCameraTargetAndZoom(LatLng cameraTarget, float zoom) {
        this.cameraTarget = cameraTarget;
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }
}
