package com.hcmut.admin.bktrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class InfoVoucher {

    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("point")
    private int point;

    @SerializedName("slider")
    private List<SliderResponse> slider;
    public InfoVoucher(String name,String avatar,int point,List<SliderResponse> slider) {
        this.avatar = avatar;
        this.name = name;
        this.point = point;
        this.slider = slider;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPoint() {
        return point;
    }
    public List<SliderResponse> getSlider() {
        return slider;
    }
}
