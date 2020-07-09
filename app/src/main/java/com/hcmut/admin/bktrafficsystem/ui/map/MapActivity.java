package com.hcmut.admin.bktrafficsystem.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.hcmut.admin.bktrafficsystem.MyApplication;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.MarkerListener;
import com.hcmut.admin.bktrafficsystem.model.user.User;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.CallPhone;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.service.AppForegroundService;
import com.hcmut.admin.bktrafficsystem.ui.report.CameraPhoto;
import com.hcmut.admin.bktrafficsystem.ui.viewrReport.ViewReportFragment;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;
import com.stepstone.apprating.listener.RatingDialogListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by User on 10/2/2017.
 */

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        RatingDialogListener {
    private MyApplication myapp;

    public static User currentUser;
    public static GoogleMap mMap;
    public static AndroidExt androidExt = new AndroidExt();
    private Date pressTime;

    // Listener
    private ViewReportFragment.OnReportMakerClick reportMakerClickListener;
    private MarkerListener markerListener;
    private RatingDialogListener ratingDialogListener;
    private List<OnMapReadyListener> onMapReadyListeners = new ArrayList<>();

    private SupportMapFragment mapFragment;
    private BottomTab bottomTab;
    private BottomNavigation bottomNavigation;
    private FrameLayout flFragment;
    private androidx.fragment.app.Fragment homeFragment;
    private androidx.fragment.app.Fragment reportFragment;
    private androidx.fragment.app.Fragment viewReportFragment;
    private androidx.fragment.app.Fragment accountReportFragment;

    public void setUserReportMarkerListener(ViewReportFragment.OnReportMakerClick listener) {
        reportMakerClickListener = listener;
    }

    public void setMarkerListener(MarkerListener markerListener) {
        this.markerListener = markerListener;
    }

    public void addMapReadyCallback(@NotNull OnMapReadyListener onMapReadyListener) {
        if (mMap != null) {
            onMapReadyListener.onMapReady(mMap);
        } else {
            onMapReadyListeners.add(onMapReadyListener);
        }
    }

    public void setRatingDialogListener(RatingDialogListener ratingDialogListener) {
        this.ratingDialogListener = ratingDialogListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myapp = (MyApplication) this.getApplicationContext();
        currentUser = SharedPrefUtils.getUser(MapActivity.this);

        addCotrols();
        addEvents();
    }

    private void addCotrols() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        homeFragment = getSupportFragmentManager().findFragmentById(R.id.homeFragmentTab);
        reportFragment = getSupportFragmentManager().findFragmentById(R.id.reportFragmentTab);
        viewReportFragment = getSupportFragmentManager().findFragmentById(R.id.viewReportFragmentTab);
        accountReportFragment = getSupportFragmentManager().findFragmentById(R.id.accountFragmentTab);

        View homeTabWrapper = findViewById(R.id.homeTabWrapper);
        View reportTabWrapper = findViewById(R.id.reportTabWrapper);
        View viewReportTabWrapper = findViewById(R.id.viewReportTabWrapper);
        View accountTabWrapper = findViewById(R.id.accountTabWrapper);
        bottomTab = new BottomTab.Builder(R.id.homeTabId)
                .addTab(R.id.reportTabId, reportTabWrapper)
                .addTab(R.id.homeTabId, homeTabWrapper)
                .addTab(R.id.viewReportTabId, viewReportTabWrapper)
                .addTab(R.id.accountTabId, accountTabWrapper)
                .build();
    }

    /**
     *
     * Add Events
     */
    private void addEvents() {
        boolean gpsCollectionSwithValue = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.swGpsCollectionRef), true);
        if (gpsCollectionSwithValue) {
            ProbeForgroundServiceManager.initLocationService(this);
        }

        flFragment = findViewById(R.id.flFragment);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        flFragment.setPadding(0, 0, 0, bottomNavigation.getNavigationHeight());
        bottomNavigation.setMenuItemSelectionListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int menuItemId, int i1, boolean b) {
                bottomTab.showTab(menuItemId);
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(ProbeMapUi.MAX_ZOOM_LEVEL);
        mMap.setMyLocationEnabled(true);
        for (OnMapReadyListener listener : onMapReadyListeners) {
            listener.onMapReady(googleMap);
        }
        onMapReadyListeners.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    switch (marker.getTag().toString()) {
                        case ViewReportFragment.REPORT_RATING: {
                            if (reportMakerClickListener != null) {
                                reportMakerClickListener.onClick(marker);
                            }
                            break;
                        }
                        case MarkerListener.REPORT_ARROW:
                            if (markerListener != null) {
                                markerListener.onClick(marker);
                            }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        myapp.setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        clearActivity();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearActivity();
        mMap = null;
        androidExt = null;
        super.onDestroy();
    }

    private void clearActivity() {
        Activity currActivity = myapp.getCurrentActivity();
        if (this.equals(currActivity))
            myapp.setCurrentActivity(null);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showBottomNav() {
        if (bottomNavigation.getVisibility() != View.VISIBLE) {
            bottomNavigation.setVisibility(View.VISIBLE);
            flFragment.setPadding(0, 0, 0, bottomNavigation.getNavigationHeight());
        }
    }

    public void hideBottomNav() {
        if (bottomNavigation.getVisibility() == View.VISIBLE) {
            bottomNavigation.setVisibility(View.GONE);
            flFragment.setPadding(0, 0, 0, 0);
        }
    }

    public int getBottomNavHeight() {
        return bottomNavigation.getNavigationHeight();
    }

    public GoogleMap getGoogleMap() {
        return mMap;
    }

    @Override
    public void onBackPressed() {
        if (onNavigationBackPress(homeFragment) ||
                onNavigationBackPress(reportFragment) ||
                onNavigationBackPress(viewReportFragment) ||
                onNavigationBackPress(accountReportFragment)) {
            return;
        }

        Date currentTime = new Date();
        if (pressTime == null) {
            pressTime = currentTime;
            Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
            return;
        }
        long alpha = TimeUnit.MILLISECONDS.toMillis(currentTime.getTime() - pressTime.getTime());
        if (alpha < 8000) {
            super.onBackPressed();
        } else {
            pressTime = currentTime;
            Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean onNavigationBackPress(androidx.fragment.app.Fragment fragment) {
        try {
            OnBackPressCallback callback = (OnBackPressCallback) fragment
                    .getChildFragmentManager()
                    .getFragments().get(0);
            callback.onBackPress();
            return true;
        } catch (Exception e) {}
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CameraPhoto.IMAGE_REQUEST:
                CameraPhoto.onActivityResult(getApplicationContext(), resultCode, data);
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ProbeForgroundServiceManager.MUTILE_PERMISSION_REQUEST:
                ProbeForgroundServiceManager.handleAppForgroundPermission(MapActivity.this, requestCode, permissions, grantResults);
                return;
            case CallPhone.CALL_PHONE_CODE:
                boolean isHavePermission = CallPhone.handleCallPhonePermission(grantResults);
                if (isHavePermission) {
                    CallPhone.makeAPhoneCall(MapActivity.this);
                }
                return;
            case CameraPhoto.IMAGE_PERMISSION_CODE:
                CameraPhoto.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults,
                        MapActivity.this);
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onNegativeButtonClicked() {
        if (ratingDialogListener != null) {
            ratingDialogListener.onNegativeButtonClicked();
        }
    }

    @Override
    public void onNeutralButtonClicked() {
        if (ratingDialogListener != null) {
            ratingDialogListener.onNeutralButtonClicked();
        }
    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        if (ratingDialogListener != null) {
            ratingDialogListener.onPositiveButtonClicked(i, s);
        }
    }

    public interface OnMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    public interface OnBackPressCallback {
        void onBackPress();
    }
}