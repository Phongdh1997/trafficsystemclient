package com.hcmut.admin.bktrafficsystem.ui.map;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.service.AppForegroundService;

public class ProbeForgroundServiceManager {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 888;
    public static final int MUTILE_PERMISSION_REQUEST = 777;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private Activity activity;

    public ProbeForgroundServiceManager(Activity activity) {
        this.activity = activity;
    }

    /**
     *
     */
    public void initLocationService() {
        if (isServiceRunning()) {
            return;
        }

        // check Google play service
        if (!isPlayServicesInstalled()) {
            // notification to user about google play service is not installed
            Toast.makeText(activity, "Google play service is not installed", Toast.LENGTH_SHORT).show();
            return;
        }

        // check Location permission
        if (!hasPermisson(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, MUTILE_PERMISSION_REQUEST);
        } else {
            startLocationService(activity.getApplicationContext());
        }
    }

    public boolean handleAppForgroundPermission(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MUTILE_PERMISSION_REQUEST) {
            if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService(activity.getApplicationContext());
            }
            return true;
        }
        return false;
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AppForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     *  @return:    true if google play service is installed else return false
     */
    private boolean isPlayServicesInstalled() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                activity.finish();
            }
            return false;
        }
        return true;
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

    public static void startLocationService(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 26) {
                context.startService(new Intent(context.getApplicationContext(), AppForegroundService.class));
            } else {
                context.startForegroundService(new Intent(context.getApplicationContext(), AppForegroundService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopLocationService(Context context) {
        try {
            context.stopService(new Intent(context.getApplicationContext(), AppForegroundService.class));
        } catch (Exception e) {

        }
    }
}
