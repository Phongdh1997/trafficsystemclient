package com.hcmut.admin.bktrafficsystem.model;

import com.google.android.gms.maps.model.Marker;

public interface MarkerListener {
    String REPORT_ARROW = "REPORT_ARROW";
    void onClick(Marker marker);
}
