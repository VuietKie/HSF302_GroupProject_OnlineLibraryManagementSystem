package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/pending")
    public String viewPendingBooks(Model model) {
        model.addAttribute("pendingBooks", bookService.getPendingBooks());
        return "pending-books"; // Thymeleaf template
    }

    @PostMapping("/approve/{id}")
    public String approveBook(@PathVariable("id") Long id) {
        bookService.approveBook(id);
        return "redirect:/admin/books/pending";
    }

    @PostMapping("/reject/{id}")
    public String rejectBook(@PathVariable("id") Long id) {
        bookService.rejectBook(id);
        return "redirect:/admin/books/pending";
    }
}
