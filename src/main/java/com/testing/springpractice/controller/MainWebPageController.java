package com.testing.springpractice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainWebPageController {

    @GetMapping
    public String getWelcomeWebPage() {
        return "welcome_page";
    }

    @GetMapping("/login")
    public String login() {
        return "login_page";
    }

}
