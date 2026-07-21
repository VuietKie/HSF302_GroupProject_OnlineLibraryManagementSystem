package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pending-books")
@AllArgsConstructor
public class AdminPendingBookController {

    private final BookService bookService;

    // [READ] Hiển thị danh sách các sách đang chờ phê duyệt
    @GetMapping
    public String viewInactiveBooks(Model model) {
        // Lấy tất cả sách có trạng thái INACTIVE và gửi sang giao diện
        model.addAttribute("pendingBooks", bookService.findInactiveBooks());
        return "admin/pending-books";
    }

    // [UPDATE] Phê duyệt sách và chuyển trạng thái từ INACTIVE sang ACTIVE
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
