package com.proxy2808.client;

public class Proxy2808Exception extends Exception {

    private static final String DEFAULT_MESSAGE = "2808代理请求失败";
    private static final long serialVersionUID = -1833368201889569198L;

    public Proxy2808Exception() {
        super(DEFAULT_MESSAGE);
    }

    public Proxy2808Exception(Exception e) {
        super(DEFAULT_MESSAGE, e);
    }

    public Proxy2808Exception(String message) {
        super(message);
    }

    public Proxy2808Exception(String message, Throwable cause) {
        super(message, cause);
    }

}
