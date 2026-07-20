package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.PendingBookRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    private final BookRepository bookRepository;
    private final PendingBookRepository pendingBookRepository;

    @GetMapping
    public String viewInactiveBooks(Model model) {
        model.addAttribute("pendingBooks", pendingBookRepository.findByStatus(BookStatus.INACTIVE));
        return "admin/pending-books";
    }

    @PostMapping("/activate/{id}")
    @Transactional
    public String activateBook(@PathVariable Long id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, "Không tìm thấy sách"));

        String currentEmail = authentication.getName();
        String addedByEmail = book.getAddedBy() == null ? null : book.getAddedBy().getEmail();
        if (addedByEmail != null && addedByEmail.equalsIgnoreCase(currentEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Bạn không thể approve sách do chính mình thêm.");
            return "redirect:/admin/pending-books";
        }

        if (book.getStatus() == BookStatus.INACTIVE) {
            book.setStatus(BookStatus.ACTIVE);
            bookRepository.save(book);
            redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển sách sang ACTIVE.");
        }

        return "redirect:/admin/pending-books";
    }
}
