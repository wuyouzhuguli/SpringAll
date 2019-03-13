package cc.mrbird.demo.domain;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author MrBird
 */
public class Fish {

    public Fish() {
        System.out.println("调用无参构造器创建Fish");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化Fish");
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁Fish");
    }
}
