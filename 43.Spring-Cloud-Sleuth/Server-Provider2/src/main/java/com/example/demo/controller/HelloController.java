package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String hello() {
        logger.info("调用server-provider2的hello接口");
        return "hello world";
    }
}
