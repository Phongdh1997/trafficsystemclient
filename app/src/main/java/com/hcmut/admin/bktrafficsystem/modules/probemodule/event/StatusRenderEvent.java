package com.hcmut.admin.bktrafficsystem.modules.probemodule.event;

public class StatusRenderEvent {
    private boolean isRendering = true;
    private boolean isClearRender = true;

    public StatusRenderEvent() {
    }

    public StatusRenderEvent(boolean isClearRender) {
        this.isClearRender = isClearRender;
    }

    public boolean isClearRender() {
        return isClearRender;
    }
}
