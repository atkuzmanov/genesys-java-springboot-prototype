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

/**
 * Difficulty injecting Request scope object into @Aspect
 * - <https://stackoverflow.com/questions/41151546/inject-request-scope-object-into-aspect>
 *
 * Difficulty adding the traceId from Spring Cloud Sleuth to response in Filter
 * - <https://stackoverflow.com/questions/41222405/adding-the-traceid-from-spring-cloud-sleuth-to-response>
 * - <https://cloud.spring.io/spring-cloud-sleuth/2.0.x/single/spring-cloud-sleuth.html#__literal_tracingfilter_literal>
 * - <https://blog.michaelstrasser.com/2017/07/using-sleuth-trace-id/>
 * - <https://github.com/robert07ravikumar/sleuth-sample/blob/master/src/main/java/com/example/demo/AddResponseHeaderFilter.java>
 */
@Component
@Configuration
public class LoggingFilter extends OncePerRequestFilter {

    private final LoggingService logServ = new LoggingService();
    /* RE: Here you can exclude urls, so the response does not get logged twice, once by doFilterInternal() and once by the Aspect.
     * - <https://stackoverflow.com/questions/39212551/how-do-i-exclude-a-specific-url-in-a-filter-in-spring>
     * - <https://stackoverflow.com/questions/33864252/spring-mvc-handler-interceptor-with-exclude-path-pattern-with-pathparam>
     * - <https://www.programmergate.com/how-to-exclude-a-url-from-a-filter/>
     * TODO: Possible improvement - extract excluded urls to properties configuration for different profiles.
     */
    private static final List<String> EXCLUDE_URL = Arrays.asList("/health", "/httptrace", "/info",
            "/all", "/addTimestamp", "/updateTimestamp", "/delete");

    @Bean
    public FilterRegistrationBean<LoggingFilter> initFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());

        // *1* Make sure you sett all dispatcher types if you want the filter to log upon.
        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

        // *2* This should put your filter above any other filter.
        // Adding "+1" to make it just below the TRACING_FILTER_ORDER level, so that spanId and traceId are added.
        registrationBean.setOrder(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1);

        return registrationBean;
    }

    /**
     * Note: Regarding Distributed Tracing, Sleuth and Zipkin
     * The @traceId @spanId etc. are added when logging through the AOP Aspect.
     * If the logging is not going through there, then they need to be manually added here.
     *
     * RE: Difficulty adding the traceId from Spring Cloud Sleuth to response in Filter
     * - <https://cloud.spring.io/spring-cloud-sleuth/2.0.x/single/spring-cloud-sleuth.html#__literal_tracingfilter_literal>
     * - <https://stackoverflow.com/questions/41222405/adding-the-traceid-from-spring-cloud-sleuth-to-response>
     *
     * RE: Difficulty in ContentCachingResponseWrapper Produces Empty Response
     * - <https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response>
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
         * RE: Difficulty in ContentCachingResponseWrapper Produces Empty Response
         * - <https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response>
         */
        filterChain.doFilter(requestWrapper, responseWrapper);

        logServ.logContentCachingResponse(responseWrapper, this.getClass().getName(), "doFilterInternal");

        /* The line below is very important!
         * RE: Difficulty in ContentCachingResponseWrapper Produces Empty Response
         * - <https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response>
         */
        responseWrapper.copyBodyToResponse();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

}