package com.hcmut.admin.bktrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayMoMoResponse {
    @SerializedName("state")
    private int state;
    public PayMoMoResponse(int state) {
        this.state = state;
    }
    public int getState() {
        return state;
    }
}
