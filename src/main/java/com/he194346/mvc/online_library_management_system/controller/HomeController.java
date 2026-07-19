package com.he194346.mvc.online_library_management_system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/homepage")
    public String homepage(Authentication authentication, Model model) {
        populateHomeModel(authentication, model);
        return "homepage";
    }

    @GetMapping("/admin/manager")
    public String adminManager(Authentication authentication, Model model) {
        populateHomeModel(authentication, model);
        return "homepage";
    }

    private void populateHomeModel(Authentication authentication, Model model) {
        model.addAttribute("email",authentication.getName());
        model.addAttribute("role",authentication.getAuthorities());
        model.addAttribute("isAdmin", hasRole(authentication, "ROLE_ADMIN"));
        model.addAttribute("isReader", hasRole(authentication, "ROLE_READER"));
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
