package com.hcmut.admin.bktrafficsystem.modules.probemodule.event;

public class StatusRenderEvent {
    private boolean isRendering = true;
    private boolean isRemovePrePolyline = true;

    public StatusRenderEvent() {
    }

    public StatusRenderEvent(boolean isRemovePrePolyline) {
        this.isRemovePrePolyline = isRemovePrePolyline;
    }

    public boolean isRemovePrePolyline() {
        return isRemovePrePolyline;
    }
}
