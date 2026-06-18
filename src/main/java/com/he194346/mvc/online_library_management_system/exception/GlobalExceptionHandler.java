package com.he194346.mvc.online_library_management_system.exception;

import com.he194346.mvc.online_library_management_system.dto.user.LoginRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public String handle(CustomException e,
                         HttpServletRequest request,
                         Model model) {

        String uri = request.getRequestURI();

        // 🎯 LOGIN
        if (uri.contains("/login")) {

            switch (e.getCode()) {
                case USER_NOT_FOUND:
                    model.addAttribute("error", "Không tìm thấy tài khoản");
                    break;

                case WRONG_PASSWORD:
                    model.addAttribute("error", "Sai mật khẩu");
                    break;

                default:
                    model.addAttribute("error", "Lỗi đăng nhập");
            }

            model.addAttribute("user", new LoginRequestDTO());
            return "authenticate/login";
        }

        // 🎯 REGISTER
        if (uri.contains("/register")) {

            switch (e.getCode()) {
                case USER_ALREADY_EXISTS:
                    model.addAttribute("error", "Tài khoản đã tồn tại");
                    break;

                default:
                    model.addAttribute("error", "Lỗi đăng ký");
            }

            model.addAttribute("user", new RegisterRequestDTO());
            return "authenticate/register";
        }

        return "error-page";
    }
}