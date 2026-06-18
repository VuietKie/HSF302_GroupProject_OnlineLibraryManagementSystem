package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.UserResponseDTO;
import com.he194346.mvc.online_library_management_system.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new LoginRequestDTO());
        return "authenticate/login";
    }

//    @PostMapping("/login")
//    public String login(@Valid @ModelAttribute("user") LoginRequestDTO request,
//                        BindingResult result,
//                        Model model) {
//        if (result.hasErrors()) {
//            return "authenticate/login";
//        }
//
//        UserResponseDTO userResponse = userService.login(request);
//
//        model.addAttribute("userResponse", userResponse);
//        return "homepage";
//    }

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user", new RegisterRequestDTO());
        return "authenticate/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterRequestDTO request,
                           BindingResult result) {
        if(result.hasErrors()){
            return "authenticate/register";
        }
        userService.register(request);
        return "redirect:/login";
    }
}
