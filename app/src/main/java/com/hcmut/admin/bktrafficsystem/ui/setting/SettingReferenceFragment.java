package com.hcmut.admin.bktrafficsystem.ui.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.hcmut.admin.bktrafficsystem.R;

public class SettingReferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_reference, rootKey);
    }
}
