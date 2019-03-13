package cc.mrbird;

import cc.mrbird.demo.config.WebConfig;
import cc.mrbird.demo.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        // 返回 IOC 容器，使用注解配置，传入配置类
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        System.out.println("容器创建完毕");
        // User user = context.getBean(User.class);
        // 关闭 IOC 容器
        context.close();
    }
}

