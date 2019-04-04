package com.example.webflux;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author MrBird
 */
@Controller
public class ViewController {

    @GetMapping("flux")
    public String flux() {
        return "flux";
    }
}
