package com.hcmut.admin.bktrafficsystem.model;

import android.content.Context;
import android.location.Address;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

public class SearchPlaceHandler {

    private Context context;

    public SearchPlaceHandler(Context context) {
        this.context = context;
    }

    public LatLng getLatLngFromAddressTextInput(String searchText) {
        Address address = MapUtil.getLatLngByAddressOrPlaceName(context, searchText);
        if (address != null) {
            return new LatLng(address.getLatitude(), address.getLongitude());
        }
        return null;
    }
}
