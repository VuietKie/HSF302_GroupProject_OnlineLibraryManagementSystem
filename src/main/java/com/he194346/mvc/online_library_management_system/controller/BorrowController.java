package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.BorrowService;
import com.he194346.mvc.online_library_management_system.service.BorrowRecordService;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.he194346.mvc.online_library_management_system.repository.UserRepository;

// UC05: View Borrow History
@Controller
@RequestMapping("/reader")
@AllArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final BorrowRecordService borrowRecordService;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;

    @GetMapping("/borrow-history")
    public String borrowHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = findCurrentUser(userDetails);
        model.addAttribute("borrowHistory", borrowService.getBorrowHistoryByReader(user.getUserId()));
        return "borrow/history";
    }

    @PostMapping("/borrow-history/return/{borrowRecordId}")
    public String returnBook(@PathVariable Long borrowRecordId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User user = findCurrentUser(userDetails);
        boolean ownedRecord = borrowRecordRepository.existsOwnedBorrowRecord(borrowRecordId, user.getUserId());
        if (!ownedRecord) {
            throw new CustomException(ErrorCode.BORROW_RECORD_NOT_FOUND, "Không tìm thấy phiếu mượn của bạn");
        }

        borrowRecordService.returnBorrowRecord(borrowRecordId);
        redirectAttributes.addFlashAttribute("successMessage", "Trả sách thành công.");
        return "redirect:/reader/borrow-history";
    }

    private User findCurrentUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng");
        }
        return user;
    }
}
