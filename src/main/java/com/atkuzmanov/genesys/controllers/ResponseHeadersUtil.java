package com.atkuzmanov.genesys.controllers;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResponseHeadersUtil {

    public static HttpHeaders tracingResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Span-Export", MDC.get("X-Span-Export"));
        responseHeaders.set("X-B3-SpanId", MDC.get("X-B3-SpanId"));
        responseHeaders.set("X-B3-TraceId", MDC.get("X-B3-TraceId"));

        String appName = "";
        try {
            appName = getProps().getProperty("spring.application.name");
        } catch (IOException ioe) {
            appName = "Could not obtain service name: " + ioe.getMessage();
        }
        responseHeaders.set("serviceName", appName);


        return responseHeaders;
    }

    protected static Properties getProps() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = ResponseHeadersUtil.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);

        return properties;
    }
}
