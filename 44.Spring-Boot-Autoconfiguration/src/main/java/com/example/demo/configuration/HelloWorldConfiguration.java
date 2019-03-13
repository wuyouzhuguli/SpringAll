package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MrBird
 */
@Configuration
public class HelloWorldConfiguration {

    @Bean
    public String hello() {
        return "hello world";
    }
}
