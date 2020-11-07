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
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RestLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final LoggingService logServ = new LoggingService();

    /*----------------[Request logging]----------------*/

    /* Pointcut double dots ".." to include sub packages.
     * See: https://stackoverflow.com/questions/30335563/spring-boot-logger-aspects
     */
    @Pointcut("execution(* com.atkuzmanov.genesys.controllers..*.*(..))")
    public void requestPointcut() {
    }


    /* Injecting HttpServletRequest into a Spring AOP request
     * See: https://stackoverflow.com/questions/19271807/how-to-inject-httpservletrequest-into-a-spring-aop-request-custom-scenario
     */
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

    /*----------------[Exception logging]----------------*/

    @AfterThrowing(pointcut = ("within(com.atkuzmanov.genesys..*)"), throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        logServ.logException(e, targetClass.getName(), joinPoint.getSignature().getName());
    }
}
