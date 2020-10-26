package com.atkuzmanov.genesys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GenesysApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenesysApplication.class, args);
	}

}
