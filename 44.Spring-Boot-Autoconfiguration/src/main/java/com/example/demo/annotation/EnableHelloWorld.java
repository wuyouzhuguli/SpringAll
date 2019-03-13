package com.example.demo.annotation;

import com.example.demo.configuration.HelloWorldConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author MrBird
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// @Import(HelloWorldImportSelector.class)
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {
}
