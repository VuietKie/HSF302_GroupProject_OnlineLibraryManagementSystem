package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.user.ChangePasswordRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new LoginRequestDTO());
        return "authenticate/login";
    }

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

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("request", new ChangePasswordRequestDTO());
        return "authenticate/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("request") ChangePasswordRequestDTO request,
                                 BindingResult result,
                                 Authentication authentication) {
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Xác nhận mật khẩu không khớp");
        }

        if (request.getCurrentPassword() != null
                && request.getNewPassword() != null
                && request.getCurrentPassword().equals(request.getNewPassword())) {
            result.rejectValue("newPassword", "password.same", "Mật khẩu mới phải khác mật khẩu hiện tại");
        }

        if (result.hasErrors()) {
            return "authenticate/change-password";
        }

        try {
            userService.changePassword(authentication.getName(), request);
        } catch (CustomException e) {
            result.rejectValue("currentPassword", "password.invalid", e.getMessage());
            return "authenticate/change-password";
        }

        return "redirect:/change-password?success";
    }
}
