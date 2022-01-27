package com.springboot.controller;

import com.springboot.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v2")
public class IndexController {
    @Autowired
    private ValueGetProperties blogProperties;

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private ConfigBean2 configBean2;

    @Autowired
    private PropertySourceGet testConfigBean;
    @Autowired
    private PropertySourceGet2 testConfigBean2;

    @RequestMapping("/blog")
    public String testBlog() {
        return blogProperties.toString();
    }

    @RequestMapping("/conf")
    public String testConfig() {
        return configBean.toString();
    }

    @RequestMapping("/conf2")
    public String testConfig2() {
        return configBean2.toString();
    }

    @RequestMapping("/test")
    public String index() {
        return testConfigBean.getName() + "，" + testConfigBean.getAge();
    }

    @RequestMapping("/test2")
    public String index2() {
        return testConfigBean2.toString();
    }
}
