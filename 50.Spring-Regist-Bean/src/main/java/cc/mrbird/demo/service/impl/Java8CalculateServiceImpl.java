package cc.mrbird.demo.service.impl;

import cc.mrbird.demo.service.CalculateService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author MrBird
 */
@Service
@Profile("java8")
public class Java8CalculateServiceImpl implements CalculateService {

    @Override
    public Integer sum(Integer... value) {
        System.out.println("Java 8环境下执行");
        return Arrays.stream(value).reduce(0, Integer::sum);
    }
}
