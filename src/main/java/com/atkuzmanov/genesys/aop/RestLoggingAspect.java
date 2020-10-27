package com.atkuzmanov.genesys.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        Class targetClass = joinPoint.getTarget().getClass();
        Logger requestLogger = LoggerFactory.getLogger(targetClass);

        System.out.println(">>> targetClass" + targetClass);

//        requestDetails(joinPoint, targetClass);
    }

    private void requestDetails(JoinPoint joinPoint, Class targetClass) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
//        PostMapping methodPostMapping = method.getAnnotation(PostMapping.class);
        GetMapping methodRequestMapping = method.getAnnotation(GetMapping.class);

        RequestMapping requestMapping = (RequestMapping) targetClass.getAnnotation(RequestMapping.class);

//        if (methodRequestMapping != null) {
//            System.out.println(">>> methodRequestMapping.name(): " + methodRequestMapping.name());
//            System.out.println(">>> methodRequestMapping.consumes() : " + Arrays.toString(methodRequestMapping.consumes()));
//            System.out.println(">>> methodRequestMapping.headers()) : " + Arrays.toString(methodRequestMapping.headers()));
////            System.out.println(">>> methodRequestMapping.method()) : " + methodRequestMapping.method());
//            System.out.println(">>> methodRequestMapping.params()) : " + Arrays.toString(methodRequestMapping.params()));
//            System.out.println(">>> methodRequestMapping.path()) : " + Arrays.toString(methodRequestMapping.path()));
//            System.out.println(">>> methodRequestMapping.produces() : " + Arrays.toString(methodRequestMapping.produces()));
//            System.out.println(">>> methodRequestMapping.value()) : " + Arrays.toString(methodRequestMapping.value()));
//            System.out.println(">>> methodRequestMapping.toString() : " + methodRequestMapping.toString());
//        }

        if (requestMapping != null) {
            System.out.println(">>> requestMapping.name(): " + requestMapping.name());
            System.out.println(">>> requestMapping.consumes() : " + Arrays.toString(requestMapping.consumes()));
            System.out.println(">>> requestMapping.headers()) : " + Arrays.toString(requestMapping.headers()));
            System.out.println(">>> requestMapping.method()) : " + Arrays.toString(requestMapping.method()));
            System.out.println(">>> requestMapping.params()) : " + Arrays.toString(requestMapping.params()));
            System.out.println(">>> requestMapping.path()) : " + Arrays.toString(requestMapping.path()));
            System.out.println(">>> requestMapping.produces() : " + Arrays.toString(requestMapping.produces()));
            System.out.println(">>> requestMapping.value()) : " + Arrays.toString(requestMapping.value()));
            System.out.println(">>> requestMapping.toString() : " + requestMapping.toString());
        }

//        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
//        for (Annotation an : methodAnnotations) {
//            System.out.println(">>> annotation: " + an);
//        }


    }

    private void extractRequestDetails(RequestMapping requestMapping, Annotation annotation) {

    }

    private String getUrl(String[] urls) {
        if (urls.length == 0) return "";
        else return urls[0];
    }

    @AfterReturning(pointcut = "execution(* com.atkuzmanov.genesys.controllers.*.*(..))", returning = "result")
    public void auditInfo(JoinPoint joinPoint, Object result) {
        System.out.println(">>><<< Here! 2");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        System.out.println(request.getRequestURI());

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
            System.out.println(">>> " + requestUrl);
        }
    }
}
