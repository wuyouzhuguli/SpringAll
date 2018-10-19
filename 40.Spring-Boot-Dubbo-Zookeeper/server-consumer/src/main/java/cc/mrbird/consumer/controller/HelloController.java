package cc.mrbird.consumer.controller;

import cc.mrbird.common.api.HelloService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Reference
    private HelloService helloService;

    @GetMapping("/hello/{message}")
    public String hello(@PathVariable String message) {
        return this.helloService.hello(message);
    }
}
