package com.hcmut.admin.bktrafficsystem.modules.probemodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.main.ProbeForgroundServiceManager;

public class LocationWakefulReceiver extends BroadcastReceiver {
    public static long WAKEUP_DELAY_MILLIS = 10000;

    public static String WAKEUP_ID = "wake_up_id";
    public static int WAKEUP_LOCATION_SERVICE_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Wakeup", "Run");
        try {
            ProbeForgroundServiceManager.startLocationService(context.getApplicationContext());
        } catch (Exception e) {}
    }
}
