package com.springboot;

import com.springboot.bean.PropertySourceGet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.springboot.bean.ConfigBean;

@SpringBootApplication
@EnableConfigurationProperties({ConfigBean.class})
//@EnableConfigurationProperties({ConfigBean.class, PropertySourceGet.class})
public class Application02 {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application02.class);
		app.setAddCommandLineProperties(false);
//		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}
}
