package com.atkuzmanov.genesys.rest;

import com.atkuzmanov.genesys.aop.LoggingService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Component
@Configuration
public class LoggingFilter extends OncePerRequestFilter {

    private final LoggingService logServ = new LoggingService();
    /* Here you can exclude urls, so the response does not get logged twice,
     * once by doFilterInternal() and once by the Aspect.
     * See:
     * https://stackoverflow.com/questions/39212551/how-do-i-exclude-a-specific-url-in-a-filter-in-spring
     * TODO: Possible improvement - extract excluded urls to properties configuration for different profiles.
     */
    private static final List<String> EXCLUDE_URL = Arrays.asList("/health", "/httptrace", "/info", "/updateTimestamp");

    @Bean
    public FilterRegistrationBean<LoggingFilter> initFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());

        // *1* make sure you sett all dispatcher types if you want the filter to log upon
        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

        // *2* this should put your filter above any other filter
        registrationBean.setOrder(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1);

        return registrationBean;
    }

    /**
     * Note: Regarding Distributed Tracing, Sleuth and Zipkin
     * The @traceId @spanId etc. are added when logging through the AOP Aspect.
     * If the logging is not going through there, then they need to be manually added here.
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

        /* The line below is very important!
         */
        filterChain.doFilter(requestWrapper, responseWrapper);

        logServ.logContentCachingResponse(responseWrapper, this.getClass().getName(), "doFilterInternal");

        // The line below is very important!
        responseWrapper.copyBodyToResponse();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

}