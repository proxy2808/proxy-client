package com.proxy2808.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetProxyResponse {
    @JsonProperty("data")
    List<Proxy> proxies;

    private String status;

    @JsonProperty("msg")
    private String message;

    public boolean isSuccess() {
        return "0".equals(status);
    }

    public List<Proxy> getProxies() {
        return proxies;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "GetProxyResponse{" +
                "proxies=" + proxies +
                '}';
    }
}
