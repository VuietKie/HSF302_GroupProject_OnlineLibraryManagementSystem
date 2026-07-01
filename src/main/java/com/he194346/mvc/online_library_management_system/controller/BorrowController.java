package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reader")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping("/borrow-history")
    public String viewBorrowHistory(Model model) {
        // Hardcoded readerId for demonstration (since full login flow may not be wired up yet)
        Long readerId = 1L; 
        model.addAttribute("borrowHistory", borrowService.getBorrowHistoryByReader(readerId));
        return "borrow-history";
    }
}
