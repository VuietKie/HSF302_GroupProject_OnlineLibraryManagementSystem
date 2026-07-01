package com.he194346.mvc.online_library_management_system.exception;

import com.he194346.mvc.online_library_management_system.dto.user.RegisterRequestDTO;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public String handle(CustomException e,
                         HttpServletRequest request,
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

        model.addAttribute("error", e.getMessage());
        return "error-page";
    }

}
