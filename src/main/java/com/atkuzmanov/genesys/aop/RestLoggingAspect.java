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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Aspect
@Component
public class RestLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.atkuzmanov.genesys.controllers.*.*(..))")
    public void requestPointcut() {
    }

    @Before("requestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger requestLog = LoggerFactory.getLogger(targetClass);

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
        requestLogMap.put("method", joinPoint.getSignature().getName());
        requestLogMap.put("class", targetClass.toString());
        requestLogMap.values().removeIf(value -> value == null || value.trim().length() == 0);

        requestLog.info("INCOMING_REQUEST",
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
            logger.error(ex.getMessage(), ex);
        }
        return buffer.toString();
    }


    // TODO: wip
    @AfterReturning(pointcut = "execution(* com.atkuzmanov.genesys.controllers.*.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
//        System.out.println(">>><<< Here! 2");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

//        System.out.println(request.getRequestURI());

        if (result instanceof ResponseEntity) {
            ResponseEntity responseObj = (ResponseEntity) result;

            String requestUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath() + request.getRequestURI()
                    + "?" + request.getQueryString();

            String clientIp = request.getRemoteAddr();
//            String clientRequest = reqArg.toString();
            int httpResponseStatus = responseObj.getStatusCode().value();
//            responseObj.getEntity();
// Can log whatever stuff from here in a single spot.
//            System.out.println(">>> " + requestUrl);
        }
    }

    // TODO: log exception
    @AfterThrowing(pointcut = ("within(com.atkuzmanov.genesys..*)"), throwing = "e")
    public void logAfterThrowing(JoinPoint p, Exception e) throws Exception {
        Class<?> targetClass = p.getTarget().getClass();
        Logger exceptionLog = LoggerFactory.getLogger(targetClass);
        exceptionLog.error(p.getTarget().getClass().getSimpleName() + " " + p.getSignature().getName() + " " +
                e.getMessage());
    }
}
