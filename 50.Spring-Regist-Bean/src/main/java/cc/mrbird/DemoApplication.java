package cc.mrbird;

import cc.mrbird.demo.config.WebConfig;
import cc.mrbird.demo.service.CalculateService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context1 = new SpringApplicationBuilder(DemoApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("java7")
                .run(args);

        // 返回 IOC 容器，使用注解配置，传入配置类
        ApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        System.out.println("容器创建完毕");

        // User user = context.getBean(User.class);
        // System.out.println(user);

        // 查看 User 这个类在 Spring 容器中叫啥玩意
        // String[] beanNames = context.getBeanNamesForType(User.class);
        // Arrays.stream(beanNames).forEach(System.out::println);

        // 查看基于注解的 IOC容器中所有组件名称
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.stream(beanNames).forEach(System.out::println);

        // 组件的作用域
        // Object user1 = context.getBean("user");
        // Object user2 = context.getBean("user");
        // System.out.println(user1 == user2);

        // 测试懒加载
        // Object user1 = context.getBean("user");
        // Object user2 = context.getBean("user");

        // 测试 Profile
        CalculateService service = context1.getBean(CalculateService.class);
        System.out.println("求合结果： " + service.sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // FactoryBean测试
        Object cherry = context.getBean("cherryFactoryBean");
        System.out.println(cherry.getClass());

        Object cherryFactoryBean = context.getBean("&cherryFactoryBean");
        System.out.println(cherryFactoryBean.getClass());

    }
}

