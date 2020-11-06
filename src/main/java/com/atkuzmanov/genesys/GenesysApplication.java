package com.atkuzmanov.genesys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GenesysApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenesysApplication.class, args);
    }

    /**
     * Enables Spring Boot's actuator trace
     * http://localhost:8080/actuator/httptrace
     * See: https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-http-tracing
     *
     * @return InMemoryHttpTraceRepository
     */
    @Bean
    public HttpTraceRepository htttpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
