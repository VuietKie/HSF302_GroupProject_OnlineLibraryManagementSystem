package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pending-books")
@AllArgsConstructor
public class AdminPendingBookController {

    private final BookService bookService;

    @GetMapping
    public String viewInactiveBooks(Model model) {
        model.addAttribute("pendingBooks", bookService.findInactiveBooks());
        return "admin/pending-books";
    }

    @PostMapping("/activate/{id}")
    public String activateBook(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            bookService.approvePendingBook(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển sách sang ACTIVE.");
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
        }

        return "redirect:/admin/pending-books";
    }
}
