package com.springboot.controller;

import com.springboot.bean.ConfigBean2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.bean.ValueGetProperties;
import com.springboot.bean.ConfigBean;
import com.springboot.bean.TestConfigBean;


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
    private TestConfigBean testConfigBean;

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
}
