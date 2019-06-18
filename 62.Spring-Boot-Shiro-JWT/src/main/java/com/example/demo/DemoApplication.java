package com.example.demo;

import com.example.demo.properties.SystemProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 *
 * @author MrBird
 */
@SpringBootApplication
@EnableConfigurationProperties(SystemProperties.class)
public class DemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
