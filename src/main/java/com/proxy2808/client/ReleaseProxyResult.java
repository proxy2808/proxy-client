package com.proxy2808.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseProxyResult {

    @JsonProperty("msg")
    private String message;

    private String status;

    private ReleaseProxyResult() {}

    public ReleaseProxyResult(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public boolean isSuccess() {
        return "0".equals(status);
    }

    public static ReleaseProxyResult success(String message){
        return new ReleaseProxyResult(message, "0");
    }

    public static ReleaseProxyResult fail(String message) {
        return new ReleaseProxyResult(message, "0");
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ReleaseProxyResult{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
