package com.example.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // SpringApplication application = new SpringApplication(DemoApplication.class);
        // application.setBannerMode(Banner.Mode.OFF);
        // application.setWebApplicationType(WebApplicationType.NONE);
        // application.setHeadless(true);
        // application.run(args);

        new SpringApplicationBuilder(DemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
