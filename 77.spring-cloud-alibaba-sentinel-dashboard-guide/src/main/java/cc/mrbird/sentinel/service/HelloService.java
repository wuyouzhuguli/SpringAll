package cc.mrbird.sentinel.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * @author MrBird
 */
@Service
public class HelloService {

    @SentinelResource("hello")
    public String hello() {
        return "hello";
    }
}
