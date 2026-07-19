package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.BorrowService;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.he194346.mvc.online_library_management_system.repository.UserRepository;

// UC05: View Borrow History
@Controller
@RequestMapping("/reader")
@AllArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final UserRepository userRepository;

    @GetMapping("/borrow-history")
    public String borrowHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Lấy user đang đăng nhập qua email (username)
        var user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng");
        }
        model.addAttribute("borrowHistory", borrowService.getBorrowHistoryByReader(user.getUserId()));
        return "borrow/history";
    }
}
