# GeneSys

![GeneSys Logo v1.0](GeneSys_Logo_v1.0.png)

---

## Setup

- Setting up the Maven wrapper

```sh
mvn -N io.takari:maven:wrapper
```
---

## Notes

### ASPECT LOGGING and DISTRIBUTED TRACING

This [StackOverflow question/thread](https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl) 
demonstrates how hard the problem of combining LOGGING with DISTRIBUTED TRACING is, especially when you are using ASPECT ORIENTED PROGRAMMING (AOP) with Java and Spring Boot.
Furthermore this thread is only about logging all requests, things get really "fun", when you start adding DISTRIBUTED TRACING etc.

While you have many individual options for just one or the other, the difficulty arises when you try to combine them and make them play nice.
I am referring to combining the following:
- AOP (Controller Advice, ResponseBodyAdvice etc.)
- Logging (cross-cutting concern => AOP)
- SLF4J
- MDC
- Filters (Request/Responce Filters etc.)
- Interceptors (Request/Responce Interceptors etc.)
- Handlers (Exception Handlers etc.)
- Servlets
- Spring/Spring Boot Configuration
- Dependencies
- Java version

As you can see because of the amount of topics, and the amount of possible approaches for each topic, 
the amount of possible successful combinations grows exponentially small. 
It becomes almost as trying to find a needle in a haystack, as when you have just fixed or made two of them play nice 
together, the third next thing you need for the next functionality breaks one of the previous two.

### Difficulties capturing Response body

RE: Here is one good alternative using `ResponseBodyAdvice`:

- <https://frandorado.github.io/spring/2018/11/15/log-request-response-with-body-spring.html>
- <https://github.com/frandorado/spring-projects/tree/master/log-request-response-with-body>

### Difficulties with AOP and Controlelrs

RE: Spring AOP does not work when the request is not mapped or valid
- <https://stackoverflow.com/questions/59817236/spring-aop-around-controllers-does-not-work-when-request-input-are-invalid>

### Difficulties with injecting HttpServletRequest into a Spring AOP request

- <https://stackoverflow.com/questions/19271807/how-to-inject-httpservletrequest-into-a-spring-aop-request-custom-scenario>
    And I quote:
    > "Not easily. Actually, it would require a lot of effort.
    > 
    > The easy way to do it is to rely on RequestContextHolder. In every request, the DispatcherServlet binds the current HttpServletRequest to a static ThreadLocal object in the RequestContextHolder. You can retrieve it when executing within the same Thread with
    > 
    > `HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();`                                                                                                                                           > 
    > You can do this in the advice() method and therefore don't need to declare a parameter."


### Apsects

RE: AOP

- <https://docs.spring.io/spring-framework/docs/4.3.15.RELEASE/spring-framework-reference/html/aop.html>
- <https://dzone.com/articles/implementing-aop-with-spring-boot-and-aspectj>
- <https://www.baeldung.com/spring-aop-annotation>
- <https://www.baeldung.com/spring-aop-pointcut-tutorial>
- <https://www.javaguides.net/2019/05/spring-boot-spring-aop-logging-example-tutorial.html>
- <https://stackoverflow.com/questions/30335563/spring-boot-logger-aspects>
- <http://blog.marcinchwedczuk.pl/overview-of-spring-annotation-driven-aop>
- <https://stackoverflow.com/questions/55948389/how-can-i-start-and-stop-a-timer-in-different-classes/55949813#55949813>

RE: Aspect Weaving

- <https://www.baeldung.com/aspectj>

RE: Logging Requests/Responses

- <https://www.baeldung.com/spring-http-logging>
- <https://github.com/sjkhullar/RequestLogging/blob/master/RequestLogging/src/main/java/org/learn/log/aspect/LoggingHandler.java>

### Filters

RE: Difficulty injecting Request scope object into @Aspect

- <https://stackoverflow.com/questions/41151546/inject-request-scope-object-into-aspect>

RE: Difficulty adding the traceId from Spring Cloud Sleuth to response in Filter

- <https://stackoverflow.com/questions/41222405/adding-the-traceid-from-spring-cloud-sleuth-to-response>
- <https://cloud.spring.io/spring-cloud-sleuth/2.0.x/single/spring-cloud-sleuth.html#__literal_tracingfilter_literal>
- <https://blog.michaelstrasser.com/2017/07/using-sleuth-trace-id/>
- <https://github.com/robert07ravikumar/sleuth-sample/blob/master/src/main/java/com/example/demo/AddResponseHeaderFilter.java>

RE: Filters/Filtering

- <https://www.mdeditor.tw/pl/2w1V>

### Distributed Tracing

RE: Distributed Tracing

- <https://docs.lightstep.com/docs/understand-distributed-tracing>

- <https://opensource.com/article/18/5/distributed-tracing>

- <https://copyconstruct.medium.com/distributed-tracing-weve-been-doing-it-wrong-39fc92a857df>

- <https://spring.io/blog/2016/02/15/distributed-tracing-with-spring-cloud-sleuth-and-spring-cloud-zipkin>

RE: Sleuth

- <https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/getting-started.html#getting-started>

- <https://www.baeldung.com/tracing-services-with-zipkin>

- <https://www.baeldung.com/spring-cloud-sleuth-single-application>

RE: Zipkin

- <https://zipkin.io/>

- <https://medium.com/swlh/microservices-observability-with-zipkin-and-spring-cloud-sleuth-66508ce6840>

- <https://howtodoinjava.com/spring-cloud/spring-cloud-zipkin-sleuth-tutorial/>

- <https://medium.com/swlh/distributed-tracing-in-micoservices-using-spring-zipkin-sleuth-and-elk-stack-5665c5fbecf>

- <https://examples.javacodegeeks.com/spring-cloud-zipkin-and-sleuth-example/>

RE: Correlation in Logging; Mapped Diagnostic Context (MDC); Logback; Log4j2;

- <https://medium.com/@d.lopez.j/spring-boot-setting-a-unique-id-per-request-dd648efef2b>

- <https://stackoverflow.com/questions/56338075/how-to-create-unique-id-per-request-for-rest-api-in-spring-boot>

- <https://dzone.com/articles/setting-up-mdc-context-with-aop-in-spring-boot-app>

- <https://dzone.com/articles/implementing-correlation-ids-0>

- <https://dzone.com/articles/correlation-id-for-logging-in-microservices>

- <http://www.javabyexamples.com/logging-with-request-correlation-using-mdc>

- <https://stackoverflow.com/questions/43787815/how-to-log-mdc-with-spring-sleuth>

- <https://www.baeldung.com/mdc-in-log4j-2-logback>

RE: A way to get traceId into an SLF4j MDC
- <https://github.com/openzipkin/brave/issues/150>

### Logback

- <http://logback.qos.ch/manual/introduction.html>

---

# References

> <https://spring.io/>
>
> <http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20>
>
> <https://www.mdeditor.tw/pl/2w1V>

---
