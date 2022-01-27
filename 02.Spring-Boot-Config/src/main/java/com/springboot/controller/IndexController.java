package com.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.bean.BlogProperties;
import com.springboot.bean.ConfigBean;
import com.springboot.bean.TestConfigBean;


@RestController
@RequestMapping("/v2")
public class IndexController {
    @Autowired
    private BlogProperties blogProperties;
    @Autowired
    private ConfigBean configBean;
    @Autowired
    private TestConfigBean testConfigBean;

    @RequestMapping("/blog")
    public String testBlog(){
        return blogProperties.toString();
    }

    @RequestMapping("/conf")
    public String testConfig(){
        return configBean.toString();
    }

    @RequestMapping("/test")
    public String index() {
        return testConfigBean.getName() + "，" + testConfigBean.getAge();
    }
}
