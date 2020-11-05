package com.atkuzmanov.genesys.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;


            System.out.println(">>> req: " + httpServletRequest);
            if(!(httpServletResponse instanceof ResponseEntity)) {
                System.out.println(">>> resp: " + httpServletResponse);
            }

            chain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Throwable a) {
            log.error(a.getMessage());
        }
    }
}
