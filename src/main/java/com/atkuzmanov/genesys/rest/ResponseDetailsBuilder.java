package com.atkuzmanov.genesys.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class ResponseDetailsBuilder {
    private int status;
    private String originMethod;
    private String originClass;
    private String responseBody;
    private String responseMessage;
    private String path;
    private HttpHeaders headers;
    private Throwable throwable;

    public ResponseDetailsBuilder headers(HttpHeaders httpHeaders) {
        this.headers = httpHeaders;
        return this;
    }

    public ResponseDetailsBuilder throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public ResponseDetailsBuilder originMethod(String originMethod) {
        this.originMethod = originMethod;
        return this;
    }

    public ResponseDetailsBuilder originClass(String originClass) {
        this.originClass = originClass;
        return this;
    }

    public ResponseDetailsBuilder status(int status) {
        this.status = status;
        return this;
    }

    public ResponseDetailsBuilder responseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public ResponseDetailsBuilder responseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    public ResponseDetailsBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ResponseDetails build() {
        ResponseDetails responseDetails = new ResponseDetails();
        responseDetails.setOriginClass(originClass);
        responseDetails.setOriginMethod(originMethod);
        responseDetails.setStatus(status);
        responseDetails.setPath(path);
        responseDetails.setHeaders(headers);
        responseDetails.setResponseBody(responseBody);
        responseDetails.setResponseMessage(responseMessage);
        responseDetails.setThrowable(throwable);
        return responseDetails;
    }

    public ResponseEntity<ResponseDetails> entity() {
        return ResponseEntity.status(status).headers(headers).body(build());
    }
}
