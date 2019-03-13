package cc.mrbird.demo.config;

import cc.mrbird.demo.condition.MyCondition;
import cc.mrbird.demo.domain.Hello;
import cc.mrbird.demo.domain.User;
import cc.mrbird.demo.factory.CherryFactoryBean;
import cc.mrbird.demo.filter.MyTypeFilter;
import cc.mrbird.demo.register.MyImportBeanDefinitionRegistrar;
import cc.mrbird.demo.selector.MyImportSelector;
import lombok.Builder;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author MrBird
 */
@Configuration
// @ComponentScan(value = "cc.mrbird.demo"
        // , excludeFilters = {
        //         @Filter(type = FilterType.ANNOTATION,
        //                 classes = {Controller.class, Repository.class}),
        //         @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = User.class)
        //         @Filter(type = FilterType.CUSTOM, classes = MyTypeFilter.class)
        // }
        // includeFilters = {
        //         @Filter(type = FilterType.ANNOTATION, classes = Service.class)
        // }, useDefaultFilters = false
// )
@Import({Hello.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class WebConfig {

    @Bean
    // @Conditional(MyCondition.class)
    // @Lazy
    // @Scope("prototype")
    public User user() {
        System.out.println("往IOC容器中注册user bean");
        return new User("mrbird", 18);
    }

    @Bean
    public CherryFactoryBean cherryFactoryBean() {
        return new CherryFactoryBean();
    }
}
