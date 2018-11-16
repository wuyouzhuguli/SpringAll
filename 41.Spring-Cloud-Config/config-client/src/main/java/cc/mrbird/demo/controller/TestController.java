package cc.mrbird.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {

    @Value("${message}")
    private String message;

    @GetMapping("message")
    public String getMessage() {
        return this.message;
    }
}
