package com.atkuzmanov.genesys;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseDetailsBuilder {
    private String originMethod;
    private String originClass;
    private int status;
    private String responseBody;
    private Map<String, String> headers;
    private String responseMessage;
    private String path;
    private Throwable throwable;

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

    public ResponseDetailsBuilder headers(Map<String, String> headers) {
        this.headers = headers;
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
        return responseDetails;
    }

    public ResponseEntity<ResponseDetails> entity() {
        return ResponseEntity.status(status).headers(HttpHeaders.EMPTY).body(build());
    }
}
