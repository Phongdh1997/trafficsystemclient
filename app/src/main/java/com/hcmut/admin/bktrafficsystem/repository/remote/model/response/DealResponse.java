package com.hcmut.admin.bktrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DealResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("point")
    private int point;
    @SerializedName("code")
    private String code;
    @SerializedName("content")
    private String content;
    @SerializedName("send_id")
    private UserResponse sender;
    @SerializedName("type")
    private String type;
    @SerializedName("message")
    private String message;
    @SerializedName("createdAt")
    private Date createdAt;
    public DealResponse(String id,int point,String code,String content,UserResponse sender,String type,String message,Date createdAt){
        this.id=id;
        this.point=point;
        this.code=code;
        this.content=content;
        this.sender=sender;
        this.type= type;
        this.message=message;
        this.createdAt=createdAt;
    };

    public String getId() {
        return id;
    }

    public int getPoint() {
        return point;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public UserResponse getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
}
