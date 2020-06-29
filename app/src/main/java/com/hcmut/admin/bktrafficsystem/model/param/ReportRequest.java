package com.hcmut.admin.bktrafficsystem.model.param;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.UserLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReportRequest {
    public static final String [] reasons = {
            "Tắc đường", "Ngập lụt", "Có vật cản", "Tai nạn", "Công an", "Đường cấm"
    };

    private int velocity;
    private double currentLat;
    private double currentLng;
    private double nextLat;
    private double nextLng;
    private List<String> causes;
    private String description;
    private ArrayList<String> images;
    private String type;

    public ReportRequest() {

    }

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

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getNextLat() {
        return nextLat;
    }

    public void setNextLat(double nextLat) {
        this.nextLat = nextLat;
    }

    public double getNextLng() {
        return nextLng;
    }

    public void setNextLng(double nextLng) {
        this.nextLng = nextLng;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
