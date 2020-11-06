package com.atkuzmanov.genesys.rest;

import brave.Tracer;
import com.atkuzmanov.genesys.aop.LogRequestOrResponse;
import com.atkuzmanov.genesys.aop.LoggingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.logstash.logback.argument.StructuredArguments.fields;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
@Configuration
//@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Bean
    public FilterRegistrationBean<LoggingFilter> initFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());

        // *1* make sure you sett all dispatcher types if you want the filter to log upon
        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

        // *2* this should put your filter above any other filter
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setOrder(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1);

        return registrationBean;
    }




    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Note: Regarding Distributed Tracing, Sleuth and Zipkin
     * The @traceId @spanId etc. are added when logging through the AOP Aspect.
     * If the logging is not going through there, then they need to be manuallya added here.
     * See:
     * https://cloud.spring.io/spring-cloud-sleuth/2.0.x/single/spring-cloud-sleuth.html#__literal_tracingfilter_literal
     * https://stackoverflow.com/questions/41222405/adding-the-traceid-from-spring-cloud-sleuth-to-response
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
        filterChain.doFilter(requestWrapper, responseWrapper);

//        LoggingService ls = new LoggingService();
//        ls.logContentCachingRequest(requestWrapper, this.getClass().getName(), "test1");

        String requestUrl = requestWrapper.getRequestURL().toString();
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            requestHeaders.add(headerName, requestWrapper.getHeader(headerName));
        }
        HttpMethod httpMethod = HttpMethod.valueOf(requestWrapper.getMethod());
        Map<String, String[]> requestParams = requestWrapper.getParameterMap();

        String requestBody = IOUtils.toString(requestWrapper.getInputStream(),UTF_8);
//        JsonNode requestJson = objectMapper.readTree(requestBody);

//        RequestEntity<JsonNode> requestEntity = new RequestEntity<>(requestJson,requestHeaders, httpMethod, URI.create(requestUrl));
//        LOGGER.info(appendFields(requestEntity),"Logging Http Request");


        HttpStatus responseStatus = HttpStatus.valueOf(responseWrapper.getStatus());
        HttpHeaders responseHeaders = new HttpHeaders();
        for (String headerName : responseWrapper.getHeaderNames()) {
            responseHeaders.add(headerName, responseWrapper.getHeader(headerName));
        }
        String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8);
//        JsonNode responseJson = objectMapper.readTree(responseBody);
        String str = objectMapper.writeValueAsString(responseBody);
        ResponseEntity<?> responseEntity = new ResponseEntity<>(str,responseHeaders,responseStatus);
//        LOGGER.info("<<< Logging Http Response >>>", fields(responseEntity));

        LoggingService ls = new LoggingService();
        ls.logContentCachingResponse(responseWrapper, this.getClass().getName(), "doFilterInternal");

        // The line below is very important!
        responseWrapper.copyBodyToResponse();
    }
}