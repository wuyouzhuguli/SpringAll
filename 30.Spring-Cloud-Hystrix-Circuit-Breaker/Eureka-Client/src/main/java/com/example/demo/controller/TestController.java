package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscoveryClient client;

    @GetMapping("/info")
    public String info() {
        @SuppressWarnings("deprecation")
        ServiceInstance instance = client.getLocalServiceInstance();
        String info = "host：" + instance.getHost() + "，service_id：" + instance.getServiceId();
        log.info(info);
        return info;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
