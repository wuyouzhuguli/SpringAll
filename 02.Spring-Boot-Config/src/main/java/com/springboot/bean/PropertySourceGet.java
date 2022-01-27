package com.springboot.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: wyq
 * @create time: 2022/1/27
 * @description: @PropertySource读取指定 properties文件
 * @PropertySource不支持加载yml配置文件，所以需要自定义一个配置类
 */
@ConfigurationProperties(prefix = "test")
@PropertySource("classpath:property.properties")
//@PropertySource("classpath:property.yml") //此时获取name和age分别为null，0
@Component
public class PropertySourceGet {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
