package com.proxy2808.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    @JsonProperty("msg")
    private String message;
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private LoginResponseData loginResponseData;

    public Boolean isSuccess() {
        return "0".equals(status);
    }

    public static class LoginResponseData {
        private String token;

        public String getToken() {
            return token;
        }

        @Override
        public String toString() {
            return "LoginResponseData{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }

    public String getMessage() {
        return message;
    }

    public LoginResponseData getLoginResponseData() {
        return loginResponseData;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", loginResponseData=" + loginResponseData +
                '}';
    }
}
