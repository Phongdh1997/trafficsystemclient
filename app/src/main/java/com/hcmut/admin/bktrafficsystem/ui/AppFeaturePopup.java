package com.hcmut.admin.bktrafficsystem.ui;

import android.content.Context;
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

public class AppFeaturePopup {
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

        // init view
        CardView cardView = popupView.findViewById(R.id.card);
        Button btnReport = popupView.findViewById(R.id.btn_report);

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
    }

    public void showPopup() {
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
}
