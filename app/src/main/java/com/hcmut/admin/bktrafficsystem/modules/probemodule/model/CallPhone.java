package com.hcmut.admin.bktrafficsystem.modules.probemodule.model;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

import java.lang.ref.WeakReference;

public class CallPhone {
    public static final int CALL_PHONE_CODE = 888;
    private final String[] permissions = {Manifest.permission.CALL_PHONE};

    private WeakReference<MapActivity> mapActivityWeakReference;

    public CallPhone(MapActivity mapActivity) {
        mapActivityWeakReference = new WeakReference<>(mapActivity);
    }

    private boolean hasPermisson(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    public void checkCallPhonePermisstion () {
        MapActivity activity = mapActivityWeakReference.get();
        if (activity == null) return;
        if (!hasPermisson(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, CALL_PHONE_CODE);
        } else {
            makeAPhoneCall(activity);
        }
    }

    public static boolean handleCallPhonePermission(int[] grantResults) {
        return (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    public static void makeAPhoneCall(MapActivity mapActivity) {
        if (mapActivity != null) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0123456789"));
            mapActivity.startActivity(intent);
        }
    }
}
