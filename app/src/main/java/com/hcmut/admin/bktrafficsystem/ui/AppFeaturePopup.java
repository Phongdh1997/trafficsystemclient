package com.hcmut.admin.bktrafficsystem.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hcmut.admin.bktrafficsystem.R;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

public class AppFeaturePopup {
    public static final int CALL_PHONE_CODE = 888;
    private final String[] permissions = {Manifest.permission.CALL_PHONE};

    private WeakReference<MapActivity> mapActivityWeakReference;
    private View popupView;
    private PopupWindow popupWindow;

    public AppFeaturePopup(MapActivity context) {
        mapActivityWeakReference = new WeakReference<>(context);
        popupView = LayoutInflater.from(context).inflate(R.layout.app_feature_popup_layout, null, false);
        popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setElevation(2.3f);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);

        // init view
        CardView cardView = popupView.findViewById(R.id.card);
        Button btnReport = popupView.findViewById(R.id.btn_report);
        Button btnCallPhoneReport = popupView.findViewById(R.id.btnCallPhoneReport);

        // add action
        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });    // disable card view touch
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = mapActivityWeakReference.get();
                if (mapActivity != null) {
                    mapActivity.doReport();
                }
                popupWindow.dismiss();
            }
        });
        btnCallPhoneReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity mapActivity = mapActivityWeakReference.get();
                if (mapActivity != null) {
                    checkCallPhonePermisstion(mapActivity, permissions);
                }
            }
        });
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

    private void checkCallPhonePermisstion (Activity activity, String [] permissions) {
        if (!hasPermisson(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, CALL_PHONE_CODE);
        } else {
            makeAPhoneCall();
        }
    }

    public boolean handleCallPhonePermission(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_PHONE_CODE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                makeAPhoneCall();
            }
            return true;
        }
        return false;
    }

    private void makeAPhoneCall() {
        MapActivity mapActivity = mapActivityWeakReference.get();
        if (mapActivity != null) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0123456789"));
            mapActivity.startActivity(intent);
        }
    }

    public void showPopup() {
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
}
