package com.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
	}
}
