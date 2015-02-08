package org.addhen.smssync.net;

public enum HttpMethod {
    POST("POST"),
    GET("GET"),
    PUT("PUT");

    private final String mMethod;

    HttpMethod(String method) {
        mMethod = method;
    }

    public String value() {
        return mMethod;
    }
}
