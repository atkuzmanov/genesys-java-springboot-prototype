package com.atkuzmanov.genesys;

import java.util.Map;

public class ResponseDetails {
    private String originMethod;
    private String originClass;
    private int status;
    private String responseBody;
    private Map<String, String> headers;
    private String responseMessage;
    private String path;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    private Throwable throwable;

    public String getOriginMethod() {
        return originMethod;
    }

    public void setOriginMethod(String originMethod) {
        this.originMethod = originMethod;
    }

    public String getOriginClass() {
        return originClass;
    }

    public void setOriginClass(String originClass) {
        this.originClass = originClass;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static ResponseDetailsBuilder builder() {
        return new ResponseDetailsBuilder();
    }
}
