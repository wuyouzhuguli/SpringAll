package cc.mrbird.sso.client.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrBird
 */
@RestController
public class UserController {

    @GetMapping("user")
    public Authentication user(Authentication authentication) {
        return authentication;
    }
}
