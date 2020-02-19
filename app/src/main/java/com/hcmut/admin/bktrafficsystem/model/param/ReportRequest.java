package com.hcmut.admin.bktrafficsystem.model.param;

import java.util.ArrayList;

public class ReportRequest {
    private float velocity;
    private double currentLat;
    private double currentLng;
    private double nextLat;
    private double nextLng;
    private ArrayList<String> causes;
    private String description;
    private ArrayList<String> images;


    public ReportRequest(float velocity, double currentLat, double currentLng, double nextLat, double nextLng, ArrayList<String> causes, String description, ArrayList<String> images) {
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
