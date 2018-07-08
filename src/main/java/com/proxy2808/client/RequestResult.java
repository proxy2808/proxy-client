package com.proxy2808.client;

public class RequestResult {

    private boolean success;
    private Integer statusCode;
    private String content;

    public RequestResult() {
    }

    public RequestResult(boolean success, Integer statusCode, String content) {
        this.success = success;
        this.statusCode = statusCode;
        this.content = content;
    }

    public static RequestResult create(int statusCode, String content) {
        if (statusCode == 200) {
            return new RequestResult(true, statusCode, content);
        } else {
            return new RequestResult(false, statusCode, content);
        }
    }

    public static RequestResult fail() {
        return new RequestResult(false, null, null);
    }

    public boolean isSuccess() {
        return success;
    }


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getContent() {
        return content;
    }
}
