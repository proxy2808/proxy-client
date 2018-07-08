package com.proxy2808.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Proxy {

    private String id;
    @JsonProperty("http_port")
    private Integer httpPort;
    private String ip;
    @JsonProperty("s5_port")
    private Integer socksPort;

    public String getId() {
        return id;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public String getIp() {
        return ip;
    }

    public Integer getSocksPort() {
        return socksPort;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "id='" + id + '\'' +
                ", httpPort=" + httpPort +
                ", ip='" + ip + '\'' +
                ", socksPort=" + socksPort +
                '}';
    }
}
