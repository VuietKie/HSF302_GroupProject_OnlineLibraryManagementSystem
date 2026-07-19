package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/librarian/returns")
@RequiredArgsConstructor
public class LibrarianBorrowController {

    private final BorrowRecordService borrowRecordService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("borrowRecords", borrowRecordService.findCurrentlyBorrowed());
        model.addAttribute("now", LocalDateTime.now());
        return "librarian/borrow-list";
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.returnBorrowRecord(id);
            redirectAttributes.addFlashAttribute("success", "Đã ghi nhận trả sách cho phiếu mượn #" + id);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/returns";
    }
}
