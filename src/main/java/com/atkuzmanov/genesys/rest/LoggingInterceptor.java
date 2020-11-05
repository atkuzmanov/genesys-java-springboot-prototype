package com.atkuzmanov.genesys.rest;

import com.atkuzmanov.genesys.aop.LogRequestOrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static net.logstash.logback.argument.StructuredArguments.fields;

//@Component
//public class LoggingInterceptor extends HandlerInterceptorAdapter {
public class LoggingInterceptor {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        return true; // Will leave the pre-handling i.e. the request to the logging Aspect.
//    }
//
////    @LogRequestOrResponse
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
////        // called after the controller has processed the request,
////        // so you might log the request here and response
//////        System.out.println("??? TEST 2" + response);
////
////        ContentCachingResponseWrapper wres =
////                new ContentCachingResponseWrapper(
////                        (HttpServletResponse) response);
////
////                ResponseDetailsBuilder rdb = null;
////        try {
////            rdb = ResponseDetails.builder()
////                    .status(response.getStatus())
////    //                .headers(response.get)
////                    .responseBody(getContentAsString(wres.getContentAsByteArray(), response.getCharacterEncoding()));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        log.info(">>>> OUTGOING_RESPONSE >>>>", fields(rdb.build()));
//    }
//
//    private String getContentAsString(byte[] buf, String charsetName) {
//        if (buf == null || buf.length == 0) {
//            return "";
//        }
//
//        try {
//            int length = Math.min(buf.length, 1000);
//
//            return new String(buf, 0, length, charsetName);
//        } catch (UnsupportedEncodingException ex) {
//            return "Unsupported Encoding";
//        }
//    }
}
