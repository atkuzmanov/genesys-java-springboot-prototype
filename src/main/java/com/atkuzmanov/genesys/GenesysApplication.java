package com.atkuzmanov.genesys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GenesysApplication {
    private static final Logger log = LoggerFactory.getLogger(GenesysApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GenesysApplication.class, args);
        log.info("Application started.");
    }
}
