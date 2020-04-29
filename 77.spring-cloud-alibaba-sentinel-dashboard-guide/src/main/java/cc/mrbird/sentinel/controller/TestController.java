package cc.mrbird.sentinel.controller;

import cc.mrbird.sentinel.service.HelloService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrBird
 */
@RestController
public class TestController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HelloService helloService;

    @GetMapping("test1")
    public String test1() {
        throw new RuntimeException("服务异常");
        // return "test1";
    }

    @GetMapping("test2")
    public String test2() {
        return "test2 " + helloService.hello();
    }

    @GetMapping("buy")
    @SentinelResource(value = "buy")
    public String buy(String goodName, Integer count) {
        return "买" + count + "份" + goodName;
    }
}