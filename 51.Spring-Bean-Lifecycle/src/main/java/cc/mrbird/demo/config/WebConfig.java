package cc.mrbird.demo.config;

import cc.mrbird.demo.domain.Bird;
import cc.mrbird.demo.domain.Fish;
import cc.mrbird.demo.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author MrBird
 */
@Configuration
public class WebConfig {

    // @Scope("prototype")
    // @Bean(initMethod = "init", destroyMethod = "destory")
    // public User user() {
    //     return new User();
    // }

    // @Bean
    // public Bird bird() {
    //     return new Bird();
    // }

    @Bean
    public Fish fish(){
        return new Fish();
    }

    @Bean
    public MyBeanPostProcessor myBeanPostProcessor () {
        return new MyBeanPostProcessor();
    }
}
