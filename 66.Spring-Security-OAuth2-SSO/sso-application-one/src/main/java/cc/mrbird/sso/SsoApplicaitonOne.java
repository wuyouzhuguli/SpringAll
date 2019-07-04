package cc.mrbird.sso;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author MrBird
 */
@EnableOAuth2Sso
@SpringBootApplication
public class SsoApplicaitonOne {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SsoApplicaitonOne.class).run(args);
    }
}
