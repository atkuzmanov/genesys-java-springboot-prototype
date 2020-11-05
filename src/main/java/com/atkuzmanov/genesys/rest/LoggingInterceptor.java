package com.atkuzmanov.genesys.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true; // Will leave the pre-handling i.e. the request to the logging Aspect.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // called after the controller has processed the request,
        // so you might log the request here
        if(!(response instanceof ResponseEntity)) {
            System.out.println("??? TEST 2");
        }

    }
}
