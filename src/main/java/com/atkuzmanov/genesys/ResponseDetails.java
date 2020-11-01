package com.atkuzmanov.genesys;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDetails {
    private int status;
    private String originMethod;
    private String originClass;
    private String responseBody;
    private String message;
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

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
