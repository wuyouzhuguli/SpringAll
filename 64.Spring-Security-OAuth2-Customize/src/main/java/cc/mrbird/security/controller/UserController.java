package cc.mrbird.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrBird
 */
@RestController
public class UserController {

    @GetMapping("index")
    public Object index(@AuthenticationPrincipal Authentication authentication){
        return authentication;
    }
}
