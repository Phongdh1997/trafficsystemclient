package com.hcmut.admin.bktrafficsystem.modules.probemodule.uifeature.main;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.viewmodel.MainViewModel;

public class ProbeMainUi {

    /**
     * external view
     */
    private AppCompatActivity activity;
    private FloatingActionButton btnCurrentLocation;

    private MainViewModel mainViewModel;

    public ProbeMainUi(AppCompatActivity activity, FloatingActionButton btnCurrentLocation) {
        this.activity = activity;
        this.btnCurrentLocation = btnCurrentLocation;

        getViewModel();
        addViewModelObserver();
        addEvents();
    }

    private void getViewModel() {
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
    }

    private void addViewModelObserver() {

    }

    private void addEvents() {
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.moveToCurrentLocation();
            }
        });
    }
}
