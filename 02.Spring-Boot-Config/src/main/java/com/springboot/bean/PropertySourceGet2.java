package com.springboot.bean;

import com.springboot.yml.MixPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: wyq
 * @create time: 2022/1/27
 * @description: @PropertySource读取指定 properties文件
 * @PropertySource不支持加载yml配置文件，所以需要自定义一个配置类
 */
@ConfigurationProperties(prefix = "test2")
@PropertySource(value = {"classpath:property.yml"}, factory = MixPropertySourceFactory.class)
@Component
public class PropertySourceGet2 {
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

    @Override
    public String toString() {
        return "PropertySourceGet2{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
