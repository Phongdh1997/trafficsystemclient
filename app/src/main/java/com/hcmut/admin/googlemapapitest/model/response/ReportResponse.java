package com.hcmut.admin.googlemapapitest.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReportResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("velocity")
    private int velocity;
    @SerializedName("description")
    private String description;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("causes")
    private ArrayList<String> causes;
    @SerializedName("userId")
    private int userId;
    @SerializedName("user")
    private ReportUser user;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("reputation")
    private float reputation;

    public String getId() {
        return id;
    }

    public int getVelocity() {
        return velocity;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public ArrayList<String> getCauseId() {
        return causes;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public float getReputation() {
        return reputation;
    }

    public ReportUser getUser() {
        return user;
    }
}
