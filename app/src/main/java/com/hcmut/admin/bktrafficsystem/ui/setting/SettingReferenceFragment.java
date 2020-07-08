package com.hcmut.admin.bktrafficsystem.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.map.ProbeForgroundServiceManager;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context activity = view.getContext();
        if (activity instanceof MapActivity) {
            if (swGpsCollectionRef != null) {
                swGpsCollectionRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if ("true".equals(newValue.toString())) {
                            ProbeForgroundServiceManager.initLocationService((MapActivity) activity);
                        } else {
                            ProbeForgroundServiceManager.stopLocationService(getContext());
                        }
                        return true;
                    }
                });
            }
        }

        if (swAutoSleepWakeupRef != null) {
            swAutoSleepWakeupRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    return true;
                }
            });
        }
        if (swNotifyAroundRef != null) {
            swNotifyAroundRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    return true;
                }
            });
        }
        if (swNotifyMoveRef != null) {
            swNotifyMoveRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    return true;
                }
            });
        }
    }
}
