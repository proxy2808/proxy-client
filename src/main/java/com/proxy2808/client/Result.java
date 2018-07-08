package com.proxy2808.client;

public class Result<T> {

    private String message;
    private boolean success;
    private T data;
    private Proxy2808Exception exception;

    Result() {}

    public Result(String message, boolean success, T data, Proxy2808Exception e) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.exception = e;
    }


    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", data=" + data +
                ", exception=" + exception +
                '}';
    }
}
