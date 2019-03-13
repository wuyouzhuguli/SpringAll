package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @author MrBird
 */
// @RestController
@Controller
public class TestController {

    @GetMapping(value = "test", consumes = "text/properties")
    @ResponseBody
    public Properties getUser(@RequestBody Properties properties) {
        return properties;
    }

    @GetMapping(value = "test1", consumes = "text/properties")
    // @ResponseBody
    public Properties getUser1(Properties properties) {
        return properties;
    }
}
