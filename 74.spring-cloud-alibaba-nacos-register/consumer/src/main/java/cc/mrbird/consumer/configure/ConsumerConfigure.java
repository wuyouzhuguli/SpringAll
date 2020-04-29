package cc.mrbird.consumer.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author MrBird
 */
@Configuration
public class ConsumerConfigure {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
