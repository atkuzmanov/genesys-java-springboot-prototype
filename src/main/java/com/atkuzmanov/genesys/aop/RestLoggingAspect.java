package com.atkuzmanov.genesys.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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
import java.util.Enumeration;

@Aspect
@Component
public class RestLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final Logger logger = LoggerFactory.getLogger("jsonConsoleAppender");

    @Pointcut("execution(* com.atkuzmanov.genesys.controllers.*.*(..))")
    public void requestPointcut() {
    }

    // TODO: wip
    @Before("requestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        System.out.println(">>><<< Here!");
        Class targetClass = joinPoint.getTarget().getClass();
        Logger requestLogger = LoggerFactory.getLogger(targetClass);
//        Logger requestLogger = LoggerFactory.getLogger(this.getClass());
        requestLogger.info("<<< request logger!!");
//        requestLogger.debug("<<< request logger!!");

        System.out.println(">>> targetClass" + targetClass);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        System.out.println(">>> request.getRequestURI()): " + request.getRequestURI());
        System.out.println(">>> request.getMethod()): " + request.getMethod());
        System.out.println(">>> request.getScheme()): " + request.getScheme());
        System.out.println(">>> request.getProtocol()): " + request.getProtocol());
        System.out.println(">>> request.getLocalName()): " + request.getLocalName());
        System.out.println(">>> request.getLocalAddr()): " + request.getLocalAddr());
        System.out.println(">>> request.getLocale())): " + request.getLocale());
        System.out.println(">>> request.getLocales())): " + request.getLocales());
        System.out.println(">>> request.getQueryString()): " + request.getQueryString());
        System.out.println(">>> request.getLocalName()): " + request.getLocalName());
        System.out.println(">>> request.getServletPath()): " + request.getServletPath());
        System.out.println(">>> request.getRemoteHost()): " + request.getRemoteHost());
        System.out.println(">>> request.getServerName()): " + request.getServerName());
        System.out.println(">>> request.getServerPort()): " + request.getServerPort());

        logger.info("<<< request.getRequestURI()): " + request.getRequestURI());
        logger.info("{}", request.getLocalAddr());
        logger.info("Hello, World JSON!");
        logger.debug("Hello World JSON.");

        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            System.out.println("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }

        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                System.out.println(headerName + " : " + request.getHeader(headerName));
            }
        }

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
        String payload = buffer.toString();
        System.out.println(">>> payload : " + payload);
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
}
