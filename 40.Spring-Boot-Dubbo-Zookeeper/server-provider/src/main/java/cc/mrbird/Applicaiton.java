package cc.mrbird;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
public class Applicaiton {
    public static void main(String[] args) {
        SpringApplication.run(Applicaiton.class, args);
        System.out.println("complete");
    }
}
