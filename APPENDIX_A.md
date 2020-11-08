# APPENDIX A

## NOTES

### ASPECT LOGGING, STRUCTURED LOGGING and DISTRIBUTED TRACING

This [StackOverflow question/thread](https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl) 
demonstrates how hard the problem of combining LOGGING with DISTRIBUTED TRACING is, especially when you are using ASPECT ORIENTED PROGRAMMING (AOP) with Java and Spring Boot.
Furthermore, this thread is only about logging all requests, things get really "fun", when you start adding DISTRIBUTED TRACING etc.

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
- <https://github.com/frandorado/spring-projects/blob/master/log-request-response-with-body/src/main/java/com/frandorado/loggingrequestresponsewithbody/interceptor/CustomResponseBodyAdviceAdapter.java>

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


### Apsects Aspect Oriented Programming (AOP)

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

RE: Difficulty in ContentCachingResponseWrapper Produces Empty Response
- <https://stackoverflow.com/questions/39935190/contentcachingresponsewrapper-produces-empty-response>

RE: Here you can exclude urls, so the response does not get logged twice, once by doFilterInternal() and once by the Aspect.
- <https://stackoverflow.com/questions/39212551/how-do-i-exclude-a-specific-url-in-a-filter-in-spring>
- <https://stackoverflow.com/questions/33864252/spring-mvc-handler-interceptor-with-exclude-path-pattern-with-pathparam>
- <https://www.programmergate.com/how-to-exclude-a-url-from-a-filter/>

RE: Bypassing the HttpServletRequest issue
RE: Adding CachedBodyHttpServletRequest to Spring Boot Filter
- <https://dfar.io/recording-http-request-body-with-java-spring-boot-and-application-insights/>

RE: Filters/Filtering

- <https://www.baeldung.com/spring-boot-add-filter>
- <https://www.mdeditor.tw/pl/2w1V>
- <https://gist.github.com/int128/e47217bebdb4c402b2ffa7cc199307ba>

### Interceptors/Intercepting

RE: Exclude Spring Request HandlerInterceptor by Path-Pattern
- <https://stackoverflow.com/questions/34970179/exclude-spring-request-handlerinterceptor-by-path-pattern/34974725>

RE: Interceptor
- <https://github.com/glaudiston/spring-boot-rest-payload-logging>
- <https://github.com/glaudiston/spring-boot-rest-payload-logging/blob/master/src/main/java/com/example/restservice/util/LogApiInterceptor.java>
- <https://stackoverflow.com/questions/21193380/get-requestbody-and-responsebody-at-handlerinterceptor>

RE: Filter Interceptor Request Response Wrapping

- <https://github.com/gadapa-rakesh/spring-request-response-body-auditing>

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

### Exception Handling

RE: Difficulty in missing errors

RE: Spring-Boot: Missing logs on HTTP 500
> If you don't handle the exception at all then it will become an error dispatch, i.e. filters are executed a second time with the DispatcherType.ERROR trying to delegate to an error page. By default you don't get any from Spring, iirc.
>  
>  Error dispatch is a tricky beast to get right, especially with Logbook. See #32, #155 and #334. Past experiences can be best summarized as Don't use ERROR dispatch.
>  
>  Instead I'd suggest to use custom @ExceptionHandlers or something like https://github.com/zalando/problem-spring-web.
- <https://github.com/zalando/logbook/issues/488>

RE: Problems for Spring MVC and Spring WebFlux

- <https://github.com/zalando/problem-spring-web>

RE: Customize HTTP Error Responses in Spring Boot
- <https://dzone.com/articles/customize-error-responses-in-spring-boot>

RE: Spring Boot Exception Handling for REST and MVC

- <https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc>
- <https://www.toptal.com/java/spring-boot-rest-api-error-handling>
- <https://objectpartners.com/2014/10/21/logging-rest-exceptions-with-spring/>
- <https://mflash.dev/blog/2020/07/26/error-handling-for-a-spring-based-rest-api/>
- <https://www.springboottutorial.com/spring-boot-exception-handling-for-rest-services>
- <https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#server-properties>

RE: Error Handling for REST with Spring

- <https://www.baeldung.com/exception-handling-for-rest-with-spring>

### Structured Logging

RE: Logback + Logstash + Configuration
- <http://logback.qos.ch/manual/introduction.html>
- <https://github.com/mafr/structured-logging/blob/master/spring-boot/logback-spring.xml>
- <https://github.com/mafr/structured-logging>
- <https://stackoverflow.com/questions/57104502/how-do-i-wrap-some-of-the-methods-of-logstash-logback-encoder-into-an-inner-fiel>
- <https://stackoverflow.com/questions/51729062/how-to-add-custom-field-to-log-logback-logstash>
- <https://github.com/logstash/logstash-logback-encoder/blob/master/src/test/resources/logback-test.xml>
- <https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/>
- <https://stackoverflow.com/questions/61735308/how-to-configure-custom-patternlayout-of-a-loggingeventcompositejsonencoder>
- <https://stackoverflow.com/questions/59718108/how-can-i-change-a-key-name-in-the-json-output-using-logback>

RE: Logging in JSON general
- <https://stackoverflow.com/questions/53730449/is-there-a-recommended-way-to-get-spring-boot-to-json-format-logs-with-logback>
- <https://www.baeldung.com/spring-boot-logging>
- <https://www.baeldung.com/java-log-json-output>
- <https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/howto-logging.html>

RE: JSON Logging for Spring Boot + Logstash
- <http://development.wombatsecurity.com/development/2018/12/20/json-logging-for-spring-boot/>
- <https://www.innoq.com/en/blog/structured-logging/>
- <https://sadique.io/blog/2016/06/30/json-logging-for-spring-applications/>
- <https://gquintana.github.io/2017/12/01/Structured-logging-with-SL-FJ-and-Logback.html>

RE: Structured Logging
- <https://medium.com/@krishankantsinghal/structured-logging-why-and-how-to-achieve-in-java-33974d22accb>
- <https://medium.com/@d.lopez.j/easily-connecting-your-spring-boot-applications-to-the-elastic-stack-with-log4j2-1809e81c6a2e>
- <https://medium.com/hprog99/spring-boot-logs-with-elasticsearch-logstash-and-kibana-elk-c61a378f3cb4>
- <https://www.schibsted.pl/blog/logback-pattern-gdpr/>
- <https://blog.10pines.com/2020/03/02/log-custom-json-with-log4j2/>

### Maven

RE: Maven Enforcer Plugin
- <https://blogs.oracle.com/developers/mastering-maven-the-enforcer-plugin>
- <https://maven.apache.org/enforcer/maven-enforcer-plugin/>

RE:--illegal-access=deny
RE: Illegal reflective access
- <https://github.com/spring-projects/spring-framework/issues/22814>
- <https://github.com/spring-projects/spring-framework/issues/25382>

### Spring application.properties

RE: spring.jpa.open-in-view Warning
- <https://stackoverflow.com/questions/30549489/what-is-this-spring-jpa-open-in-view-true-property-in-spring-boot>
- <https://www.baeldung.com/spring-open-session-in-view>

### Design Patterns

- <https://www.oodesign.com/>

---
