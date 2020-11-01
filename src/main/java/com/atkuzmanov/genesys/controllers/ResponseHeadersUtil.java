package com.atkuzmanov.genesys.controllers;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//@Component
public class ResponseHeadersUtil {

//    @Value("${application.name}")
//    private static String applicationName;

    public static HttpHeaders tracingResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Span-Export", MDC.get("X-Span-Export"));
        responseHeaders.set("X-B3-SpanId", MDC.get("X-B3-SpanId"));
        responseHeaders.set("X-B3-TraceId", MDC.get("X-B3-TraceId"));

        String appName = "";
        try {
            appName = getProps().getProperty("spring.application.name");
        } catch (IOException ioe) {
            appName = ioe.getMessage();
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
