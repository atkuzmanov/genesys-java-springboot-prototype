package com.atkuzmanov.genesys.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toString();
    }
}
