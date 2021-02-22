package com.hcmut.admin.bktrafficsystem.ui.setting;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.service.AppForegroundService;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.business.GPSForegroundServiceHandler;
import com.hcmut.admin.bktrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

public class SettingReferenceFragment extends PreferenceFragmentCompat {
    SwitchPreference swGpsCollectionRef;
    SwitchPreference swAutoSleepWakeupRef;
    SwitchPreference swNotifyAroundRef;
    SwitchPreference swNotifyMoveRef;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_reference, rootKey);

        swGpsCollectionRef = findPreference(getResources().getString(R.string.swGpsCollectionRef));
        swAutoSleepWakeupRef = findPreference(getResources().getString(R.string.swAutoSleepWakeupRef));
        swNotifyAroundRef = findPreference(getResources().getString(R.string.swNotifyAroundRef));
        swNotifyMoveRef = findPreference(getResources().getString(R.string.swNotifyMoveRef));
    }

    private void setEnableNotificationOption(boolean value) {
        swNotifyAroundRef.setEnabled(value);
        swNotifyMoveRef.setEnabled(value);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context activity = view.getContext();
        if (activity instanceof MapActivity) {
            if (swGpsCollectionRef != null) {
                swGpsCollectionRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        MapActivity mapActivity = (MapActivity) activity;
                        if ("true".equals(newValue.toString())) {
                            if (MapUtil.checkGPSTurnOn(mapActivity, MapActivity.androidExt)) {
                                GPSForegroundServiceHandler.initLocationService(mapActivity);
                                setEnableNotificationOption(true);
                                return true;
                            }
                            return false;
                        }
                        GPSForegroundServiceHandler.stopLocationService(getContext());
                        setEnableNotificationOption(false);
                        return true;
                    }
                });
            }
        }

        if (swAutoSleepWakeupRef != null) {
            swAutoSleepWakeupRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleSleepWakeup("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }
        if (swNotifyAroundRef != null) {
            swNotifyAroundRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleReportNoti("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }
        if (swNotifyMoveRef != null) {
            swNotifyMoveRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleDirectionNoti("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }

        if (swGpsCollectionRef != null && !swGpsCollectionRef.isChecked()) {
            setEnableNotificationOption(false);
        }
    }
}
