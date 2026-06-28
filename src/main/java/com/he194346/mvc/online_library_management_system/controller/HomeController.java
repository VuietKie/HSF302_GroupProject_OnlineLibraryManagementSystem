package com.he194346.mvc.online_library_management_system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/homepage")
    public String homepage(Authentication authentication, Model model) {
        model.addAttribute("email",authentication.getName());
        model.addAttribute("role",authentication.getAuthorities());
        return "homepage";
    }
}
