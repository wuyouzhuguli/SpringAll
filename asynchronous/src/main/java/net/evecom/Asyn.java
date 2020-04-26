package net.evecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Asyn {

    public static void main(String[] args) {
        SpringApplication.run(Asyn.class,args);
    }


}
