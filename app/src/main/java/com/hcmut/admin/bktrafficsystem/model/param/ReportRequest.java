package com.hcmut.admin.bktrafficsystem.model.param;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReportRequest {
    private int velocity;
    private double currentLat;
    private double currentLng;
    private double nextLat;
    private double nextLng;
    private ArrayList<String> causes;
    private String description;
    private ArrayList<String> images;
    private String type;


    /**
     * Contructor for post GPS data
     * @param prevLocation
     * @param currentLocation
     */
    public ReportRequest(@NotNull UserLocation prevLocation, @NotNull UserLocation currentLocation) {
        this.currentLat = prevLocation.getLatitude();
        this.currentLng = prevLocation.getLongitude();
        this.nextLat = currentLocation.getLatitude();
        this.nextLng = currentLocation.getLongitude();
        this.velocity = Math.round(UserLocation.calculateSpeed(prevLocation, currentLocation));
        this.type = "system";
    }

    public ReportRequest(int velocity, double currentLat, double currentLng, double nextLat, double nextLng, ArrayList<String> causes, String description, ArrayList<String> images) {
        this.velocity = velocity;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.nextLat = nextLat;
        this.nextLng = nextLng;
        this.causes = causes;
        this.description = description;
        this.images = images;
    }

}
