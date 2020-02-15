package com.hcmut.admin.googlemapapitest.model.param;

public class LoginParam {
    private String userId;
    private String token;
    private int loginType;

    public LoginParam(String userId, String token, int loginType) {
        this.userId = userId;
        this.token = token;
        this.loginType = loginType;
    }
}
