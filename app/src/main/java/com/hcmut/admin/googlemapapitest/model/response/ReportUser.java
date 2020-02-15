package com.hcmut.admin.googlemapapitest.model.response;

import com.google.gson.annotations.SerializedName;

public class ReportUser {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("__v")
    private float reputation;

    public String getId() {
        return id;
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
}
