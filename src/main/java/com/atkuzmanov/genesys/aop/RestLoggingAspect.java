package com.atkuzmanov.genesys.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.atkuzmanov.genesys.controllers.*.*(..))")
    public void requestPointcut() {
    }

    @Before("requestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        System.out.println(">>><<< Here!");
    }
}
