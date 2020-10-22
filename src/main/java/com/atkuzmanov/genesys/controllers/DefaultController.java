package com.atkuzmanov.genesys.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        return localDateTimeNowFormat_yyyyMMdddHHmmss();
    }

    private static String localDateTimeNowFormat_yyyyMMdddHHmmss() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatter);
    }
}
