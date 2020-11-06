package com.atkuzmanov.genesys.aop;

import com.atkuzmanov.genesys.rest.ResponseDetails;
import com.atkuzmanov.genesys.rest.ResponseDetailsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.persistence.criteria.Join;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.logstash.logback.argument.StructuredArguments.*;

/**
 * TODO: Documentation - add comments
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE - 1)
public class RestLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final LoggingService logServ = new LoggingService();

    /*----------------[Request logging]----------------*/

    @Pointcut("execution(* com.atkuzmanov.genesys.controllers..*.*(..))")
    public void requestPointcut() {
    }

    @Before("requestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Class<?> targetClass = joinPoint.getTarget().getClass();

//        Map<String, String> requestLogMap = new HashMap<>();
//        requestLogMap.put("uri", request.getRequestURI());
//        requestLogMap.put("url", request.getRequestURL().toString());
//        requestLogMap.put("path", request.getServletPath());
//        requestLogMap.put("requestMethod", request.getMethod());
//        requestLogMap.put("requestScheme", request.getScheme());
//        requestLogMap.put("request Protocol", request.getProtocol());
//        requestLogMap.put("request LocalName", request.getLocalName());
//        requestLogMap.put("request Locale", request.getLocale().toString());
//        requestLogMap.put("request Locales", Collections.list(request.getLocales()).toString());
//        requestLogMap.put("request QueryString", request.getQueryString());
//        requestLogMap.put("request RemoteHost ", request.getRemoteHost());
//        requestLogMap.put("request ServerName ", request.getServerName());
//        requestLogMap.put("request ServerPort ", String.valueOf(request.getServerPort()));
//        requestLogMap.put("originMethod", joinPoint.getSignature().getName());
//        requestLogMap.put("originClass", targetClass.toString());
//        requestLogMap.values().removeIf(value -> value == null || value.trim().length() == 0);
//
//        log.info("INCOMING_REQUEST",
//                entries(requestLogMap),
//                kv("queryParameters", extractRequestParameters(request)),
//                kv("requestBody", extractRequestPayload(request)),
//                kv("headers", extractRequestHeaders(request))
//        );

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        logServ.logContentCachingRequest(requestWrapper, targetClass.getName(), joinPoint.getSignature().getName());
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

    /*----------------[Response logging]----------------*/


//    @AfterReturning(pointcut = "@annotation(com.atkuzmanov.genesys.aop.LogRequestOrResponse) && args(request, response, handler, ex)",
//            returning = "result")
//    public void logInterceptedResponse(JoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response,
//                                       Object handler, Exception ex, Object result) {
//
//        System.out.println(">>> HERE " + response);
//
//        String originClass = joinPoint.getTarget().getClass().toString();
//        String originMethod = joinPoint.getSignature().getName();
//
//
//
//
//        ContentCachingResponseWrapper wres =
//                new ContentCachingResponseWrapper(
//                        (HttpServletResponse) response);
//
//
//        ResponseDetailsBuilder rdb = null;
//        try {
//            rdb = ResponseDetails.builder()
//                    .status(response.getStatus())
//                    .originClass(originClass)
//                    .originMethod(originMethod)
//    //                .headers(response.get)
//                    .responseBody(wres.getContentAsByteArray().toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        log.info(">>>> OUTGOING_RESPONSE >>>>", fields(rdb.build()));
//    }





    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) {
            return "";
        }

        try {
            int length = Math.min(buf.length, 1000);

            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }


    @AfterReturning(pointcut = "execution(* com.atkuzmanov.genesys.controllers..*.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            String originClass = targetClass.toString();
            String originMethod = joinPoint.getSignature().getName();
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;

//            if (responseEntity.hasBody()) {
//                if (responseEntity.getBody() instanceof ResponseDetails) {
//                    logResponseWithResponseDetails(responseEntity);
//                } else {
//                    logResponseWithJSONBody(responseEntity, originClass, originMethod);
//                }
//            } else {
//                log.info("OUTGOING_RESPONSE", fields(
//                        buildResponseDetailsForLogging(responseEntity, originClass, originMethod)));
//            }

            logServ.logResponseEntity(responseEntity, originClass, originMethod);

        } else {
            // Everything else is captured by the LoggingInterceptor.
//            log.info("UNKNOWN_RESPONSE", kv("unknownResponse", result));
        }
    }

    private void logResponseWithResponseDetails(ResponseEntity<?> responseEntity) {
        ResponseDetails responseDetails = (ResponseDetails) responseEntity.getBody();
        if (!this.log.isDebugEnabled()) {
            responseDetails.setThrowable(null);
        }
        log.info("OUTGOING_RESPONSE", kv("responseDetails", responseDetails));
    }

    private void logResponseWithJSONBody(ResponseEntity<?> responseEntity, String originClass, String originMethod) {
        String bodyJSONstr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            bodyJSONstr = objectMapper.writeValueAsString(responseEntity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("OUTGOING_RESPONSE", fields(
                buildResponseDetailsForLogging(responseEntity, bodyJSONstr, originClass, originMethod)));
    }

    private ResponseDetails buildResponseDetailsForLogging(ResponseEntity<?> responseObj, String originClass, String originMethod) {
        return buildResponseDetailsForLogging(responseObj, null, originClass, originMethod);
    }

    private ResponseDetails buildResponseDetailsForLogging(ResponseEntity<?> responseObj, String body, String originClass, String originMethod) {
        ResponseDetailsBuilder rdb = ResponseDetails.builder()
                .status(responseObj.getStatusCodeValue())
                .originClass(originClass)
                .originMethod(originMethod)
                .headers(responseObj.getHeaders())
                .responseBody(body);

        return rdb.build();
    }

    /*----------------[Response logging 2]----------------*/

//    @Before("execution(* com.atkuzmanov.genesys..*.*(..)) && @annotation(com.atkuzmanov.genesys.aop.LogRequestOrResponse) && args(httpServletRequest, httpServletResponse, filterChain)")
//    @Around("execution(* com.atkuzmanov.genesys.rest.LoggingFilter.doLog(..))")
//    @Around("@annotation(com.atkuzmanov.genesys.aop.LogRequestOrResponse)")
//    public void logFilterResponse(ProceedingJoinPoint joinPoint) {
//    public void logFilterResponse(JoinPoint joinPoint, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {

    @Before("execution(* com.atkuzmanov.genesys.rest.LoggingFilter.doLog(..))")
    public void logBlah(JoinPoint joinPoint) {

        System.out.println(">>> HERE!");
        log.info(">>> HERE! 2");

        try {
//            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
//
//        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        HttpStatus responseStatus = HttpStatus.valueOf(responseWrapper.getStatus());
//        HttpHeaders responseHeaders = new HttpHeaders();
//        for (String headerName : responseWrapper.getHeaderNames()) {
//            responseHeaders.add(headerName, responseWrapper.getHeader(headerName));
//        }
//        String str = null;
//        try {
//            String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8);
//            str = objectMapper.writeValueAsString(responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ResponseEntity<?> responseEntity = new ResponseEntity<>(str,responseHeaders,responseStatus);
//        log.info("<<< Logging Http Response >>>", fields(responseEntity));
    }

    /*----------------[Exception logging]----------------*/

    @AfterThrowing(pointcut = ("within(com.atkuzmanov.genesys..*)"), throwing = "e")
    public void logAfterThrowing(JoinPoint p, Exception e) {
        Class<?> targetClass = p.getTarget().getClass();
//        StringBuilder sb = new StringBuilder();
//        sb.append("Exception: ").append(p.getTarget().getClass());
//        sb.append(".").append(p.getSignature().getName()).append(": ");
//        sb.append(" Exception message: ").append(e.getMessage());
//        sb.append(" Exception cause: ").append(e.getCause());
//
//        if (this.log.isDebugEnabled()) {
//            sb.append(" Exception stacktrace: ");
//            sb.append(Arrays.toString(e.getStackTrace()));
//        }
//        log.error(sb.toString(),
//                kv("originMethod", p.getSignature().getName()),
//                kv("originClass", targetClass.toString()));

        logServ.logException(e, targetClass.getName(), p.getSignature().getName());
    }
}
