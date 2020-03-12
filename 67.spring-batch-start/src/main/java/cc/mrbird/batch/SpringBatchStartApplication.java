package cc.mrbird.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchStartApplication.class, args);
    }

}
