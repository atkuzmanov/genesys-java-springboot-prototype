package com.atkuzmanov.genesys.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDetails {
    private int status;
    private String originMethod;
    private String originClass;
    private String responseBody;
    private String responseMessage;
    private String path;
    private HttpHeaders headers;
    private Throwable throwable;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

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

    @JsonRawValue
    public String getResponseBody() {
        return responseBody;
    }

    @JsonRawValue
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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
