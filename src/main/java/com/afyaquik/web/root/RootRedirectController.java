package com.afyaquik.web.root;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping("/")
    public String redirectToHomePage() {
        return "redirect:/client/auth/index.html#/home";
    }
}
