package com.he194346.mvc.online_library_management_system.exception;

import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public String handle(CustomException e,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        String uri = request.getRequestURI();

        if (uri.equals("/register")) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", new RegisterRequestDTO());
            return "authenticate/register";
        }

        if (e.getCode() == ErrorCode.BOOK_NOT_FOUND && uri.startsWith("/reader/books")) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reader/books";
        }

        HttpStatus httpStatus = e.getCode().getHttpStatus();
        response.setStatus(httpStatus.value());
        model.addAttribute("status", httpStatus.value());
        model.addAttribute("error", httpStatus.getReasonPhrase());
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Model model) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        response.setStatus(httpStatus.value());
        model.addAttribute("status", httpStatus.value());
        model.addAttribute("error", httpStatus.getReasonPhrase());
        model.addAttribute("message", "Đã xảy ra lỗi hệ thống");
        return "error";
    }

}
