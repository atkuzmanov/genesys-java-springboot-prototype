package com.atkuzmanov.genesys.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;

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
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        logServ.logContentCachingRequest(requestWrapper, targetClass.getName(), joinPoint.getSignature().getName());
    }

    /*----------------[Response logging]----------------*/

    @AfterReturning(pointcut = "execution(* com.atkuzmanov.genesys.controllers..*.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            String originClass = targetClass.getName();
            String originMethod = joinPoint.getSignature().getName();
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;

            logServ.logResponseEntity(responseEntity, originClass, originMethod);

        }
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
    public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        logServ.logException(e, targetClass.getName(), joinPoint.getSignature().getName());
    }
}
