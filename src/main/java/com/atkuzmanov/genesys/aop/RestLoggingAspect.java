package com.atkuzmanov.genesys.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static net.logstash.logback.argument.StructuredArguments.*;

@Aspect
@Component
public class RestLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.atkuzmanov.genesys.controllers.*.*(..))")
    public void requestPointcut() {
    }

    @Before("requestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        Map<String, String> requestLogMap = new HashMap<>();
        requestLogMap.put("uri", request.getRequestURI());
        requestLogMap.put("url", request.getRequestURL().toString());
        requestLogMap.put("path", request.getServletPath());
        requestLogMap.put("requestMethod", request.getMethod());
        requestLogMap.put("requestScheme", request.getScheme());
        requestLogMap.put("request Protocol", request.getProtocol());
        requestLogMap.put("request LocalName", request.getLocalName());
        requestLogMap.put("request Locale", request.getLocale().toString());
        requestLogMap.put("request Locales", Collections.list(request.getLocales()).toString());
        requestLogMap.put("request QueryString", request.getQueryString());
        requestLogMap.put("request RemoteHost ", request.getRemoteHost());
        requestLogMap.put("request ServerName ", request.getServerName());
        requestLogMap.put("request ServerPort ", String.valueOf(request.getServerPort()));
        requestLogMap.put("originMethod", joinPoint.getSignature().getName());
        requestLogMap.put("originClass", targetClass.toString());
        requestLogMap.values().removeIf(value -> value == null || value.trim().length() == 0);

        log.info("INCOMING_REQUEST",
                entries(requestLogMap),
                kv("queryParameters", extractRequestParameters(request)),
                kv("requestBody", extractRequestPayload(request)),
                kv("headers", extractRequestHeaders(request))
        );
    }

    private Map<String, String> extractRequestParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            parameters.put(paramName, request.getParameter(paramName));
        }
        return parameters;
    }

    private Map<String, String> extractRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    private String extractRequestPayload(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
                buffer.append(System.lineSeparator());
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return buffer.toString();
    }


    @AfterReturning(pointcut = "execution(* com.atkuzmanov.genesys.controllers.*.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        if(result instanceof ResponseEntity) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            ResponseEntity responseObj = (ResponseEntity) result;

            Map<String, String> responseLogMap = new HashMap<>();
            responseLogMap.put("status", String.valueOf(responseObj.getStatusCodeValue()));
            responseLogMap.put("originMethod", joinPoint.getSignature().getName());
            responseLogMap.put("originClass", targetClass.toString());
            if (responseObj.hasBody()) {
                responseLogMap.put("responseBody", responseObj.getBody().toString());
            }

            log.info("OUTGOING_RESPONSE",
                    entries(responseLogMap),
                    kv("headers", extractResponseHeaders(responseObj)));
        }
    }

    private Map<String, String> extractResponseHeaders(ResponseEntity response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaders().forEach((key, value) -> headers.put(key, Arrays.toString(value.toArray())));
        return headers;
    }

    @AfterThrowing(pointcut = ("within(com.atkuzmanov.genesys..*)"), throwing = "e")
    public void logAfterThrowing(JoinPoint p, Exception e) {
        Class<?> targetClass = p.getTarget().getClass();
        StringBuilder sb = new StringBuilder();
        sb.append("Exception: ").append(p.getTarget().getClass());
        sb.append(".").append(p.getSignature().getName()).append(": ");
        sb.append("Exception message: ").append(e.getMessage());
        sb.append("Exception cause: ").append(e.getCause());

        if (this.log.isDebugEnabled()) {
            sb.append("Exception stacktrace: ");
            sb.append(Arrays.toString(e.getStackTrace()));
        }
        log.error(sb.toString(),
                kv("originMethod", p.getSignature().getName()),
                kv("originClass", targetClass.toString()));
    }
}
